package com.cherrylie.tiktokaia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TikTokAIAssistantApp(::openTikTokConnect) }
    }

    private fun openTikTokConnect() {
        val url = BuildConfig.BACKEND_URL.trimEnd('/') + "/oauth/tiktok/start"
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

@Composable
fun TikTokAIAssistantApp(onConnectTikTok: () -> Unit) {
    var botEnabled by remember { mutableStateOf(false) }
    var connected by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        Scaffold {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("TikTok AI Assistant", fontSize = 28.sp)

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(if (connected) "TikTok Connected" else "TikTok belum terhubung")
                                Text(
                                    if (connected) "@connected_account"
                                    else "Hubungkan akun melalui otorisasi TikTok resmi",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onConnectTikTok,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Link, null)
                            Spacer(Modifier.width(8.dp))
                            Text("CONNECT TIKTOK")
                        }
                    }
                }

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(18.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SmartToy, null)
                                Spacer(Modifier.width(8.dp))
                                Text("AI Bot")
                            }
                            Text(
                                if (botEnabled) "Aktif di dashboard" else "Nonaktif",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = botEnabled,
                            onCheckedChange = { botEnabled = it }
                        )
                    }
                }

                Text(
                    "Catatan: connect TikTok dapat memberi akses data yang disetujui oleh scope. " +
                    "Integrasi DM tidak otomatis tersedia hanya karena login berhasil.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
