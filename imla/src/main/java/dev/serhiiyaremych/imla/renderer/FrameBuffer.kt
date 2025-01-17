/*
 * Copyright 2024, Serhii Yaremych
 * SPDX-License-Identifier: MIT
 */

@file:Suppress("unused")

package dev.serhiiyaremych.imla.renderer

import androidx.compose.ui.unit.IntSize
import dev.serhiiyaremych.imla.renderer.opengl.buffer.OpenGLFramebuffer

internal enum class Bind {
    READ, DRAW, BOTH
}

internal enum class FramebufferTextureFormat {
    // bw masks, noise
    R8,

    // Color
    RGBA8,

    // Depth/stencil
    DEPTH24STENCIL8,
}

internal data class FramebufferTextureSpecification(
    val format: FramebufferTextureFormat = FramebufferTextureFormat.RGBA8,
    val flip: Boolean = false
)

internal data class FramebufferAttachmentSpecification(
    val attachments: List<FramebufferTextureSpecification> = listOf(
        FramebufferTextureSpecification(format = FramebufferTextureFormat.RGBA8),
    )
)

internal data class FramebufferSpecification(
    val size: IntSize,
    val attachmentsSpec: FramebufferAttachmentSpecification,
    val downSampleFactor: Int = 1,
)

internal interface Framebuffer {
    val rendererId: Int
    val specification: FramebufferSpecification
    val colorAttachmentTexture: Texture2D

    fun invalidate()
    fun bind(bind: Bind)
    fun unbind()
    fun resize(width: Int, height: Int)

    fun clearAttachment(attachmentIndex: Int, value: Int)
    fun getColorAttachmentRendererID(index: Int = 0): Int
    fun readBuffer(attachmentIndex: Int)

    fun destroy()
    fun setColorAttachmentAt(attachmentIndex: Int)

    companion object {
        fun create(spec: FramebufferSpecification): Framebuffer {
            return OpenGLFramebuffer(spec)
        }
    }
}
