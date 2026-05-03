package com.example.myprofileapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myprofileapp.components.EditProfileDialog
import com.example.myprofileapp.data.ProfileUiState
import myprofileapp.composeapp.generated.resources.Res
import myprofileapp.composeapp.generated.resources.download
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

val NeuPinkProfile = Color(0xFFFF90E8)
val NeuYellowProfile = Color(0xFFFFE600)
val NeuGreenProfile = Color(0xFF4ADE80)
val NeuBorderProfile = Color(0xFF000000)
val NeuBgLightProfile = Color(0xFFF4F4F0)
val NeuBgDarkProfile = Color(0xFF1E1E1E)

@Composable
fun ProfileScreen(uiState: ProfileUiState, onEditProfile: (String, String) -> Unit, onToggleDarkMode: (Boolean) -> Unit) {
    var showEditDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val deviceInfo: com.example.myprofileapp.platform.DeviceInfo = koinInject()
    val batteryInfo: com.example.myprofileapp.platform.BatteryInfo = koinInject()
    val batteryLevel by batteryInfo.observeBatteryLevel().collectAsState(initial = 0)
    val isCharging by batteryInfo.observeChargingStatus().collectAsState(initial = false)

    val isDark = uiState.isDarkMode
    val bgColor = if (isDark) NeuBgDarkProfile else NeuBgLightProfile
    val cardColor = if (isDark) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDark) Color.White else NeuBorderProfile

    if (showEditDialog) {
        fun EditProfileDialog(
            currentName: String,
            currentBio: String,
            isDark: Boolean,
            onDismiss: () -> Unit,
            onSave: (String, String) -> Unit
        ) {
            var name by remember { mutableStateOf(currentName) }
            var bio by remember { mutableStateOf(currentBio) }

            Dialog(onDismissRequest = onDismiss) {
                Box(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    // Efek Bayangan Kaku (Brutalism Shadow)
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .offset(8.dp, 8.dp)
                            .background(NeuBorderDialog, RoundedCornerShape(12.dp))
                    )

                    // Kotak Utama
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(4.dp, NeuBorderDialog, RoundedCornerShape(12.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "EDIT PROFIL",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeuBorderDialog,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Input Nama
                        Text("Nama Lengkap", fontWeight = FontWeight.Bold, color = NeuBorderDialog)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorderDialog, RoundedCornerShape(8.dp)))
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).border(3.dp, NeuBorderDialog, RoundedCornerShape(8.dp)),
                                textStyle = TextStyle(color = NeuBorderDialog, fontWeight = FontWeight.Bold),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Input Bio
                        Text("Bio", fontWeight = FontWeight.Bold, color = NeuBorderDialog)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorderDialog, RoundedCornerShape(8.dp)))
                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).border(3.dp, NeuBorderDialog, RoundedCornerShape(8.dp)),
                                textStyle = TextStyle(color = NeuBorderDialog, fontWeight = FontWeight.Bold),
                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                                maxLines = 3
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Action
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = NeuPinkDialog),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.border(3.dp, NeuBorderDialog, RoundedCornerShape(8.dp)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Batal", color = NeuBorderDialog, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { onSave(name, bio) },
                                colors = ButtonDefaults.buttonColors(containerColor = NeuGreenDialog),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.border(3.dp, NeuBorderDialog, RoundedCornerShape(8.dp)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("Simpan", color = NeuBorderDialog, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(bgColor).verticalScroll(scrollState).padding(bottom = 80.dp)) {
        GlobalOfflineBanner()

        Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp, bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(120.dp)) {
                Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorderProfile, CircleShape))
                Image(
                    painter = painterResource(Res.drawable.download),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp).clip(CircleShape).border(4.dp, NeuBorderProfile, CircleShape).background(NeuYellowProfile),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(uiState.name, fontSize = 28.sp, fontWeight = FontWeight.Black, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.background(NeuGreenProfile, RoundedCornerShape(8.dp)).border(2.dp, NeuBorderProfile, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                Text(uiState.title, fontSize = 14.sp, color = NeuBorderProfile, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(uiState.bio, fontSize = 16.sp, color = textColor.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp), lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.clickable { showEditDialog = true }) {
                Box(modifier = Modifier.matchParentSize().offset(4.dp, 4.dp).background(NeuBorderProfile, RoundedCornerShape(8.dp)))
                Row(modifier = Modifier.background(NeuPinkProfile, RoundedCornerShape(8.dp)).border(3.dp, NeuBorderProfile, RoundedCornerShape(8.dp)).padding(horizontal = 24.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = NeuBorderProfile, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EDIT PROFIL", color = NeuBorderProfile, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
        }

        Text("PENGATURAN", fontSize = 20.sp, fontWeight = FontWeight.Black, color = textColor, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorderProfile, RoundedCornerShape(12.dp)))
            Row(modifier = Modifier.fillMaxWidth().background(cardColor, RoundedCornerShape(12.dp)).border(3.dp, NeuBorderProfile, RoundedCornerShape(12.dp)).padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(48.dp).background(NeuYellowProfile, RoundedCornerShape(8.dp)).border(2.dp, NeuBorderProfile, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = NeuBorderProfile)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(if (isDark) "Mode Gelap" else "Mode Terang", fontWeight = FontWeight.Black, color = textColor, fontSize = 18.sp)
                        Text("Tema tersimpan", color = textColor.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Switch(
                    checked = isDark, onCheckedChange = { onToggleDarkMode(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeuBorderProfile, checkedTrackColor = Color.White, uncheckedThumbColor = Color.White, uncheckedTrackColor = NeuBorderProfile, uncheckedBorderColor = NeuBorderProfile)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("PERANGKAT", fontSize = 20.sp, fontWeight = FontWeight.Black, color = textColor, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorderProfile, RoundedCornerShape(12.dp)))
            Column(modifier = Modifier.fillMaxWidth().background(cardColor, RoundedCornerShape(12.dp)).border(3.dp, NeuBorderProfile, RoundedCornerShape(12.dp)).padding(vertical = 8.dp)) {
                ContactItem(Icons.Default.PhoneAndroid, "Tipe Perangkat", deviceInfo.getDeviceName(), textColor)
                HorizontalDivider(color = NeuBorderProfile, thickness = 3.dp)
                ContactItem(Icons.Default.SystemUpdate, "Sistem Operasi", deviceInfo.getOsVersion(), textColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("BATERAI", fontSize = 20.sp, fontWeight = FontWeight.Black, color = textColor, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
            Box(modifier = Modifier.matchParentSize().offset(6.dp, 6.dp).background(NeuBorderProfile, RoundedCornerShape(12.dp)))
            Column(modifier = Modifier.fillMaxWidth().background(cardColor, RoundedCornerShape(12.dp)).border(3.dp, NeuBorderProfile, RoundedCornerShape(12.dp)).padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(if (isCharging) NeuYellowProfile else NeuGreenProfile, RoundedCornerShape(8.dp)).border(2.dp, NeuBorderProfile, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(imageVector = if (isCharging) Icons.Default.Power else Icons.Default.BatteryFull, contentDescription = null, tint = NeuBorderProfile, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Status", color = textColor.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = if (isCharging) "Mengisi Daya" else "Baterai", color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(text = "$batteryLevel%", fontSize = 32.sp, fontWeight = FontWeight.Black, color = textColor)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth().height(24.dp).background(Color.White, RoundedCornerShape(50)).border(3.dp, NeuBorderProfile, RoundedCornerShape(50))) {
                    Box(modifier = Modifier.fillMaxWidth(batteryLevel / 100f).fillMaxHeight().background(if (isCharging) NeuYellowProfile else if (batteryLevel <= 20) Color.Red else NeuGreenProfile, RoundedCornerShape(50)).border(3.dp, NeuBorderProfile, RoundedCornerShape(50)))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ContactItem(icon: ImageVector, label: String, value: String, textColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(48.dp).background(NeuPinkProfile, RoundedCornerShape(8.dp)).border(2.dp, NeuBorderProfile, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = NeuBorderProfile, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, color = textColor.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(value, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}