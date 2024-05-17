/*
 * Copyright 2024, Serhii Yaremych
 * SPDX-License-Identifier: MIT
 */

package dev.serhiiyaremych.imla.renderer

import dev.serhiiyaremych.imla.renderer.primitive.QuadVertex

internal interface ShaderProgram {
    val shader: Shader
    val vertexBufferLayout: BufferLayout
    val componentsCount: Int
    fun mapVertexData(quadVertexBufferBase: List<QuadVertex>): FloatArray
}