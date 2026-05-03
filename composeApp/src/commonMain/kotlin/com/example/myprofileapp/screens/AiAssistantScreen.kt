package com.example.myprofileapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myprofileapp.data.local.ChatMessage
import com.example.myprofileapp.platform.AiService
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class AiViewModel(private val aiService: AiService) : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("Halo Piela! Ada ide cemerlang apa hari ini yang mau didiskusikan?", isUser = false))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        _messages.update { it + ChatMessage(text, isUser = true) + ChatMessage("Lagi mikir...", isUser = false, isLoading = true) }

        viewModelScope.launch {
            try {
                val reply = aiService.sendMessage(text)
                _messages.update { it.dropLast(1) + ChatMessage(reply, isUser = false) }
            } catch (e: Exception) {
                _messages.update { it.dropLast(1) + ChatMessage("Error!", isUser = false) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(isDarkMode: Boolean, onBack: () -> Unit) {
    val aiService = remember { AiService(HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }) }
    val viewModel = remember { AiViewModel(aiService) }
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    val bgColor = if (isDarkMode) Color(0xFF1E1E1E) else Color(0xFFF4F4F0)
    val neuPink = Color(0xFFFF90E8)
    val neuYellow = Color(0xFFFFE600)
    val neuBorder = Color(0xFF000000)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(0.dp), clip = false)
                    .background(neuPink).border(4.dp, neuBorder).padding(top = 32.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = neuBorder) }
                    Text("⚡ TANYA AI", fontWeight = FontWeight.Black, fontSize = 24.sp, color = neuBorder)
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).background(bgColor)) {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp), reverseLayout = true) {
                items(messages.reversed()) { msg -> NeuChatBubble(msg) }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputText, onValueChange = { inputText = it },
                    placeholder = { Text("Ketik idemu di sini...", color = Color.DarkGray, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f).shadow(4.dp, RoundedCornerShape(0.dp), clip = false).background(Color.White).border(3.dp, neuBorder),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White),
                    shape = RoundedCornerShape(0.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.sendMessage(inputText); inputText = "" },
                    modifier = Modifier.size(60.dp).shadow(4.dp, RoundedCornerShape(0.dp), clip = false).border(3.dp, neuBorder),
                    colors = ButtonDefaults.buttonColors(containerColor = neuYellow), shape = RoundedCornerShape(0.dp), contentPadding = PaddingValues(0.dp)
                ) { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = neuBorder, modifier = Modifier.size(28.dp)) }
            }
        }
    }
}

@Composable
fun NeuChatBubble(msg: ChatMessage) {
    val isUser = msg.isUser
    val bubbleBg = if (isUser) Color(0xFFFFE600) else Color.White
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val neuBorder = Color(0xFF000000)

    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), contentAlignment = alignment) {
        Box(
            modifier = Modifier.widthIn(max = 280.dp).shadow(6.dp, RoundedCornerShape(0.dp), clip = false)
                .background(bubbleBg).border(3.dp, neuBorder).padding(16.dp)
        ) {
            if (msg.isLoading) {
                Text("Lagi mikir keras... 🌀", fontWeight = FontWeight.Black, color = neuBorder)
            } else {
                Text(msg.text, fontWeight = FontWeight.Bold, color = neuBorder, fontSize = 16.sp, lineHeight = 22.sp)
            }
        }
    }
}