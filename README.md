# TikTok AI Assistant OAuth Template

Android + Node.js starter project.

## Includes
- Android Jetpack Compose dashboard
- TikTok Connect button
- Backend OAuth starter
- TikTok profile endpoint placeholder
- GitHub Actions APK build
- Java/Kotlin JVM 17 alignment

## Required before real TikTok login works
Create a TikTok Developer application and configure Login Kit.
Set these backend environment variables:

TIKTOK_CLIENT_KEY
TIKTOK_CLIENT_SECRET
TIKTOK_REDIRECT_URI
SESSION_SECRET

The current TikTok Android Login Kit flow uses OAuth and PKCE. Authorization codes should be exchanged for tokens on the server.

## Important
This template intentionally does NOT embed a TikTok client secret in the APK and does NOT implement unofficial TikTok DM scraping/automation.

## Build APK
Push to GitHub branch main. GitHub Actions uploads:
TikTok-AI-Assistant-APK

## Backend
cd backend
npm install
cp .env.example .env
npm start
