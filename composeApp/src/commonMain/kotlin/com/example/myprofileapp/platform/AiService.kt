package com.example.myprofileapp.platform

import com.example.myprofileapp.data.local.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class AiService(private val client: HttpClient) {
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent"
    private val API_KEY = "AIzaSyD2kanGAgSkjOwnKUXKbsPI0lOplWw0NLY"
    private val chatHistory = mutableListOf<Content>()
    suspend fun sendMessage(message: String): String {
        if (chatHistory.isEmpty()) {
            chatHistory.add(Content("user", listOf(Part("Konteks: Kamu adalah AI Asisten Pintar khusus untuk aplikasi Notes. Tugasmu adalah membantu pengguna mencari ide, merangkum tulisan, dan memberi saran produktivitas. Jawab dengan singkat, asyik, dan ramah dalam bahasa Indonesia."))))
            chatHistory.add(Content("model", listOf(Part("Baik, saya mengerti! Aku siap membantu."))))
        }
        chatHistory.add(Content("user", listOf(Part(message))))
        val requestBody = GeminiRequest(contents = chatHistory)
        try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                parameter("key", API_KEY)
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val geminiResponse = response.body<GeminiResponse>()
                val botReply = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Maaf, balasan AI kosong."

                chatHistory.add(Content("model", listOf(Part(botReply))))
                return botReply
            } else {
                val errorText = response.bodyAsText()
                chatHistory.removeLastOrNull()
                return "Error (${response.status.value}): $errorText"
            }
        } catch (e: Exception) {
            chatHistory.removeLastOrNull()
            return "Koneksi Gagal: Cek internetmu ya!"
        }
    }

    fun clearHistory() {
        chatHistory.clear()
    }
}