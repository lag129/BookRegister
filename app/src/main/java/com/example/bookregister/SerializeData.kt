@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.example.bookregister

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookData(
    val summary: Summary
)

@Serializable
data class Summary(
    val isbn: String,
    val title: String,
    val volume: String? = null,
    val series: String? = null,
    val publisher: String,
    val pubdate: String,
    val cover: String? = null,
    val author: String
)