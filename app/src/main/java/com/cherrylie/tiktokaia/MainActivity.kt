package com.cherrylie.tiktokaia

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

data class ChatMessage(val sender: String, val text: String, val mine: Boolean, val time: String)
data class BotSettings(
    val enabled: Boolean = false,
    val endpoint: String = "",
    val apiKey: String = "",
    val model: String = "gpt-4.1-mini",
    val persona: String = "Kamu adalah asisten chat yang ramah, singkat, natural, dan membantu.",
    val delaySeconds: Int = 3
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TikTokAIAApp() }
    }
}

@Composable
fun TikTokAIAApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    var settings by remember { mutableStateOf(loadSettings(context)) }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Raka", "Halo, boleh tanya sesuatu?", false, "21:12"),
                ChatMessage("Bot", "Tentu, silakan 😊", true, "21:12")
            )
        )
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFF00E5D6),
        secondary = Color(0xFFFF3B6B),
        background = Color(0xFF0B0B0F),
        surface = Color(0xFF15151C)
    )) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    listOf(
                        Icons.Default.Home to "Dashboard",
                        Icons.Default.Chat to "Chat",
                        Icons.Default.Settings to "Settings"
                    ).forEachIndexed { index, pair ->
                        NavigationBarItem(
                            selected = tab == index,
                            onClick = { tab = index },
                            icon = { Icon(pair.first, null) },
                            label = { Text(pair.second) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                when (tab) {
                    0 -> Dashboard(settings, { enabled ->
                        settings = settings.copy(enabled = enabled)
                        saveSettings(context, settings)
                    })
                    1 -> ChatScreen(messages)
                    2 -> SettingsScreen(settings, {
                        settings = it
                        saveSettings(context, it)
                    })
                }
            }
        }
    }
}

@Composable
fun Dashboard(settings: BotSettings, onEnabled: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("TikTok AI Assistant", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Dashboard bot dan pengaturan AI.", color = Color.Gray)

        ElevatedCard(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(if (settings.enabled) "BOT AKTIF" else "BOT NONAKTIF", fontWeight = FontWeight.Bold)
                    Text(
                        if (settings.enabled) "AI siap memproses pesan dari integrasi yang dikonfigurasi."
                        else "Auto-reply sedang dimatikan.",
                        color = Color.Gray
                    )
                }
                Switch(checked = settings.enabled, onCheckedChange = onEnabled)
            }
        }

        StatCard("Pesan hari ini", "0", Icons.Default.MailOutline)
        StatCard("Balasan AI", "0", Icons.Default.AutoAwesome)
        StatCard("Status integrasi TikTok", "Belum dikonfigurasi", Icons.Default.Link)
        Text(
            "Catatan: aplikasi ini adalah dashboard dan AI client. Pengiriman/ pembacaan DM TikTok harus dihubungkan melalui akses atau API resmi yang tersedia untuk akun dan use case kamu.",
            color = Color(0xFFFFC107),
            fontSize = 13.sp
        )
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(34.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, color = Color.Gray)
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ChatScreen(messages: List<ChatMessage>) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Conversation Log", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(messages) { msg ->
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.mine) Alignment.End else Alignment.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (msg.mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(msg.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(msg.text)
                            Text(msg.time, color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        Text(
            "Log contoh lokal. Backend dapat menambahkan percakapan nyata setelah integrasi resmi tersedia.",
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingsScreen(settings: BotSettings, onSave: (BotSettings) -> Unit) {
    var endpoint by remember(settings.endpoint) { mutableStateOf(settings.endpoint) }
    var apiKey by remember(settings.apiKey) { mutableStateOf(settings.apiKey) }
    var model by remember(settings.model) { mutableStateOf(settings.model) }
    var persona by remember(settings.persona) { mutableStateOf(settings.persona) }
    var delay by remember(settings.delaySeconds) { mutableStateOf(settings.delaySeconds.toString()) }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("AI Settings", fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("Gunakan backend milikmu sendiri. Jangan menaruh API key produksi langsung di aplikasi publik.", color = Color(0xFFFFC107), fontSize = 13.sp)

        OutlinedTextField(endpoint, { endpoint = it }, label = { Text("AI Endpoint / Backend URL") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(apiKey, { apiKey = it }, label = { Text("API Key (testing only)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(model, { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(persona, { persona = it }, label = { Text("System Prompt / Personality") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        OutlinedTextField(delay, { delay = it.filter(Char::isDigit) }, label = { Text("Reply delay (seconds)") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                onSave(
                    settings.copy(
                        endpoint = endpoint.trim(),
                        apiKey = apiKey.trim(),
                        model = model.trim(),
                        persona = persona.trim(),
                        delaySeconds = delay.toIntOrNull()?.coerceIn(0, 60) ?: 3
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("SIMPAN PENGATURAN") }

        Text("Versi 1.0.0", color = Color.Gray, fontSize = 12.sp)
    }
}

private fun loadSettings(context: Context): BotSettings {
    val p = context.getSharedPreferences("bot_settings", Context.MODE_PRIVATE)
    return BotSettings(
        enabled = p.getBoolean("enabled", false),
        endpoint = p.getString("endpoint", "") ?: "",
        apiKey = p.getString("apiKey", "") ?: "",
        model = p.getString("model", "gpt-4.1-mini") ?: "gpt-4.1-mini",
        persona = p.getString("persona", "Kamu adalah asisten chat yang ramah, singkat, natural, dan membantu.") ?: "",
        delaySeconds = p.getInt("delay", 3)
    )
}

private fun saveSettings(context: Context, s: BotSettings) {
    context.getSharedPreferences("bot_settings", Context.MODE_PRIVATE).edit()
        .putBoolean("enabled", s.enabled)
        .putString("endpoint", s.endpoint)
        .putString("apiKey", s.apiKey)
        .putString("model", s.model)
        .putString("persona", s.persona)
        .putInt("delay", s.delaySeconds)
        .apply()
}
