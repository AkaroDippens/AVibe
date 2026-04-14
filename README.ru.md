# AVibe
### Язык документации
RU | [EN](README.md) 

Android медиаплеер с эффектами slowed + reverb на основе ExoPlayer

[![Скачать APK](https://img.shields.io/badge/Скачать-APK-green)](https://github.com/AkaroDippens/AVibe/releases/latest)

---

## Требования
- Android 8.0 (API 26) или выше

---

## Скриншоты

### Тёмная тема

| Библиотека | Окно добавления | Окно переименования 
|------------|-----------------|--------|
| <img src="screenshots/library_dark.png" width="250"> | <img src="screenshots/add_file_dialog_dark.png" width="250"> | <img src="screenshots/rename_file_dialog_dark.png" width="250">

| Мини-плеер (Аудио) | Мини-плеер (Видео) | Уведомление
|--------------------|--------------------|--------|
| <img src="screenshots/mini_player_audio_dark.png" width="250"> | <img src="screenshots/mini_player_video_dark.png" width="250"> | <img src="screenshots/notification.png" width="250">

### Светлая тема

| Библиотека | Окно добавления | Окно переименования 
|---------|--------|--------|
| <img src="screenshots/library_light.png" width="250"> | <img src="screenshots/add_file_dialog_light.png" width="250"> | <img src="screenshots/rename_file_dialog_light.png" width="250">

| Мини-плеер (Аудио)
|---------|
| <img src="screenshots/mini_player_audio_light.png" width="250">

---

## Возможности
- Воспроизведение аудио (MP3) и видео (MP4) файлов из локального хранилища
- Эффект замедления в реальном времени — скорость от 0.5x до 2x
- Эффект реверберации в реальном времени — уровень от 0% до 100%
- Постоянная медиабиблиотека, сохраняющаяся между сессиями
- Мини-плеер с быстрым управлением
- Полноэкранный плеер с перемоткой
- Уведомление с управлением воспроизведением
- Тёмная и светлая тема с сохранением выбора

> [!WARNING]
> Уровень реверберации может зависеть от устройства!

---

## Технологии
- Kotlin
- Jetpack Compose
- ExoPlayer / Media3
- DataStore Preferences
- MediaSession

---

## Начало работы
1. Склонировать репозиторий
```
git clone https://github.com/AkaroDippens/AVibe.git
```
2. Открыть в Android Studio
3. Собрать и запустить на устройстве или эмуляторе (API 26+)


Или [скачайте APK](https://github.com/AkaroDippens/AVibe/releases/latest)