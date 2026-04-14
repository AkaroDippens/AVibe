# AVibe
### Documentation Language
[RU](README.ru.md) | EN

Android media player with slowed + reverb audio effects built on ExoPlayer

[![Download APK](https://img.shields.io/badge/Download-APK-green)](https://github.com/AkaroDippens/AVibe/releases/latest)

---

## Requirements
- Android 8.0 (API 26) or higher

---

## Screenshots

### Dark Theme

| Library | Add Dialog | Rename Dialog 
|---------|--------|--------|
| <img src="screenshots/library_dark.png" width="250"> | <img src="screenshots/add_file_dialog_dark.png" width="250"> | <img src="screenshots/rename_file_dialog_dark.png" width="250">

| Mini Player (Audio) | Mini Player (video) | Notification
|---------|--------|--------|
| <img src="screenshots/mini_player_audio_dark.png" width="250"> | <img src="screenshots/mini_player_video_dark.png" width="250"> | <img src="screenshots/notification.png" width="250">

### Light Theme

| Library | Add Dialog | Rename Dialog 
|---------|--------|--------|
| <img src="screenshots/library_light.png" width="250"> | <img src="screenshots/add_file_dialog_light.png" width="250"> | <img src="screenshots/rename_file_dialog_light.png" width="250">

| Mini Player (Audio)
|---------|
| <img src="screenshots/mini_player_audio_light.png" width="250">

---

## Features
- Play audio (MP3) and video (MP4) files from local storage
- Real-time slowed effect — adjust playback speed from 0.5x to 2x
- Real-time reverb effect — adjust reverb level from 0% to 100%
- Persistent media library saved between sessions
- Mini player with quick controls
- Full-screen player with seek bar
- Media notification with playback controls
- Dark and light theme with saved preference

> [!WARNING]
> Reverb level may depend on the device!

---

## Built With
- Kotlin
- Jetpack Compose
- ExoPlayer / Media3
- DataStore Preferences
- MediaSession

---

## Getting Started
1. Clone the repository
```
git clone https://github.com/AkaroDippens/AVibe.git
```
2. Open in Android Studio
3. Build and run on your device or emulator (API 26+)


Or just [download the APK](https://github.com/AkaroDippens/AVibe/releases/latest)