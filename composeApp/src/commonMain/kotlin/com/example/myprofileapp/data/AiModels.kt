package com.example.myprofileapp.data.local

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

// Ini tetap dipertahankan agar UI Chat Bubble tidak error
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isLoading: Boolean = false
)