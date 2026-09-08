# TikTok AI Assistant

Android dashboard untuk mengelola AI chat assistant dan konfigurasi integrasi backend.

## Yang sudah ada
- Dashboard bot ON/OFF
- Conversation log UI
- Pengaturan endpoint AI
- Model AI
- System prompt/personality
- Reply delay
- Penyimpanan pengaturan lokal
- GitHub Actions untuk build APK otomatis

## Yang sengaja tidak dipalsukan
Project ini **belum bisa membaca atau mengirim DM TikTok secara langsung** tanpa integrasi resmi/otorisasi yang sesuai dari TikTok. Bagian itu harus dihubungkan ke backend dan akses API yang memang tersedia untuk akun/use case kamu.

## Build di GitHub
Push semua file ke branch `main`, lalu buka:
Actions -> Build Android APK -> Artifact `TikTok-AI-Assistant-debug`

## Keamanan
Jangan menyimpan API key produksi di aplikasi Android publik. Untuk produksi gunakan backend server sebagai perantara.
