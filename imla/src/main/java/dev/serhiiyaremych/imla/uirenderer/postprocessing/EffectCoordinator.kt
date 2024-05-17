/*
 * Copyright 2024, Serhii Yaremych
 * SPDX-License-Identifier: MIT
 */

package dev.serhiiyaremych.imla.uirenderer.postprocessing

import android.content.res.AssetManager
import androidx.compose.ui.unit.Density
import dev.serhiiyaremych.imla.renderer.RenderCommand
import dev.serhiiyaremych.imla.renderer.Texture
import dev.serhiiyaremych.imla.uirenderer.RenderObject
import dev.serhiiyaremych.imla.uirenderer.postprocessing.blur.BlurEffect
import dev.serhiiyaremych.imla.uirenderer.postprocessing.noise.NoiseEffect

internal class EffectCoordinator(
    density: Density,
    private val assetManager: AssetManager
) : Density by density {

    private val effectCache: MutableMap<String, MutableList<PostProcessingEffect>> = mutableMapOf()

    private fun createEffects(renderObject: RenderObject): MutableList<PostProcessingEffect> {
        val effectSize = renderObject.layer.subTextureSize
        val style = renderObject.style

        return mutableListOf<PostProcessingEffect>().apply {
            if (style.blurRadiusPx() >= BlurEffect.MIN_BLUR_RADIUS_PX) {
                val blurEffect = BlurEffect(assetManager)
                blurEffect.setup(effectSize)
                add(blurEffect)
            }
            if (style.noiseFactor > 0f) {
                val noiseEffect = NoiseEffect(assetManager)
                add(noiseEffect)
            }
        }
    }

    fun applyEffects(renderObject: RenderObject) = with(renderObject.renderableScope) {
        val effects = effectCache.getOrPut(renderObject.id) {
            createEffects(renderObject)
        }
        var finalTexture: Texture? = null
        RenderCommand.setViewPort(0, 0, scaledSize.x.toInt(), scaledSize.y.toInt())
        effects.forEach { effect ->
            val result = effect.applyEffect(finalTexture ?: renderObject.layer)
            finalTexture = result
        }
        RenderCommand.setViewPort(0, 0, size.x.toInt(), size.y.toInt())

        val result = finalTexture
        if (result != null) {
            drawScene(cameraController.camera) {
                drawQuad(position = center, size = size, texture = result)
            }
        } else {
            drawScene(cameraController.camera) {
                drawQuad(position = center, size = size, subTexture = renderObject.layer)
            }
        }
    }

}