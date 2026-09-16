# Data Usage Monitor 📊

A lightweight, local-first Android utility app for monitoring mobile and Wi-Fi data usage directly from your device's **Quick Settings Tile** and a Jetpack Compose dashboard.

Built entirely in **Kotlin Native** using official Android system APIs (`NetworkStatsManager`, `TileService`, `DataStore Preferences`).

---

## ✨ Features

- **⚡ Quick Settings Active Tile**: Glance at today's total network usage without opening the app (`📊 1.24 GB Today`).
- **📱 Mobile vs. Wi-Fi Breakdown**: Clear separation of cellular data consumption and Wi-Fi network traffic.
- **🕒 Flexible Time Periods**: Instant filtering for **Today** (midnight to now), **This Week** (Monday to now), and **This Month** (1st of month to now) computed in local device timezone.
- **📦 Per-Application Breakdown**: Real-time UID-to-application mapping showing app icons, application names, total usage, and individual Mobile/Wi-Fi counters.
- **🔍 Fast Search & Filter**: Filter apps by network type (*All*, *Mobile*, *Wi-Fi*) and search by application name or package ID.
- **🔒 100% Local & Private**: Does **NOT** declare `android.permission.INTERNET`. All calculations and queries run strictly on-device. Zero servers, zero analytics, zero data collection.
- **🔋 Battery & Main-Thread Friendly**: All `NetworkStatsManager` queries run asynchronously on `Dispatchers.IO` with DataStore caching for instantaneous tile and app launches.
- **🎨 Modern Jetpack Compose UI**: Slate obsidian dark mode with cyan and violet neon accents, smooth animations, and Material 3 design.

---

## 📸 Experience & UX

```text
Quick Settings Panel:
┌─────────────────┐
│       📊        │
│     1.24 GB     │
│      Today      │
└────────┬────────┘
         │ (Tap tile)
         ▼
Dashboard Screen:
┌──────────────────────────────────────┐
│  Data Usage Monitor             🔄   │
├──────────────────────────────────────┤
│  [ Today  |  This Week  | This Month]│
│                                      │
│  TOTAL NETWORK USAGE                 │
│  6.06 GB                             │
│  ██████████████████████░░░░░░░░░░░   │
│  Mobile: 20%       Wi-Fi: 80%        │
│                                      │
│  ┌────────────────┐┌────────────────┐│
│  │ Mobile Data    ││ Wi-Fi Network  ││
│  │ 1.24 GB        ││ 4.82 GB        ││
│  └────────────────┘└────────────────┘│
│                                      │
│  Application Usage (5)          🔍   │
│  [ All ] [ Mobile ] [ Wi-Fi ]        │
│                                      │
│  [G] Genshin Impact         428 MB   │
│      Mobile: 350 MB • Wi-Fi: 78 MB   │
│      ████████████░░░░░░░░░░░░░░░░░   │
│                                      │
│  [I] Instagram              216 MB   │
│      Mobile: 180 MB • Wi-Fi: 36 MB   │
│      ██████░░░░░░░░░░░░░░░░░░░░░░░   │
│                                      │
│  [Y] YouTube                183 MB   │
│      Mobile: 45 MB • Wi-Fi: 138 MB   │
│      █████░░░░░░░░░░░░░░░░░░░░░░░░   │
└──────────────────────────────────────┘
```

---

## 🏛️ Architecture

The app follows Clean Architecture principles with MVVM and unidirectional data flow (UDF).

```text
┌────────────────────────────────────────────────────────┐
│                      Android OS                        │
│   NetworkStatsManager   PackageManager   TileService   │
└───────────────┬─────────────────┬──────────────┬───────┘
                │                 │              │
┌───────────────▼─────────────────▼──────────────▼───────┐
│                      Data Layer                        │
│  • NetworkStatsDataSource (UID cursor & device buckets)│
│  • UsageCache (DataStore Preferences)                  │
│  • NetworkUsageRepository (Dispatchers.IO coordinator) │
└───────────────────────────────┬────────────────────────┘
                                │ StateFlow
┌───────────────────────────────▼────────────────────────┐
│                   Presentation Layer                   │
│  • DashboardViewModel (State coordination & filtering) │
│  • DashboardUiState (Immutable UI state)               │
│  • DashboardScreen & HeroUsageCard (Jetpack Compose M3)│
│  • DataUsageTileService (Active QS tile rendering)     │
└────────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack & Android APIs

| Component | Technology | Description |
| :--- | :--- | :--- |
| **Language** | Kotlin 2.2 / 2.3 | Coroutines & Flow |
| **UI Framework** | Jetpack Compose (BOM 2024.12.01) | Material 3 declarative UI |
| **Architecture** | MVVM + StateFlow | Unidirectional data flow |
| **Network Counters** | `android.app.usage.NetworkStatsManager` | Device and per-UID byte queries |
| **Quick Settings** | `android.service.quicksettings.TileService` | Active Quick Settings tile |
| **Permissions** | `android.app.AppOpsManager` | Checks `PACKAGE_USAGE_STATS` |
| **Caching** | `androidx.datastore:datastore-preferences` | Fast persistent cache for tile |
| **Build System** | Gradle 9.1.0 + AGP 9.0.1 | Target SDK 35, Min SDK 26 |

---

## 🔐 Privacy & Permissions

Data Usage Monitor requires **Usage Access** (`android.permission.PACKAGE_USAGE_STATS`) to query Android's `NetworkStatsManager` for network traffic statistics.

> [!IMPORTANT]
> - **Zero Internet Permission**: The app does **not** include `<uses-permission android:name="android.permission.INTERNET" />`.
> - **No Background Tracking**: The app does **not** run a persistent foreground service or background polling daemon.
> - **Local Data Only**: All network stats are read directly from system counters and cached in private app storage.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat or command-line SDK
- JDK 17 or JDK 21
- Android device or emulator running Android 8.0+ (API 26+)

### Build & Run
```bash
# Clone the repository
git clone https://github.com/AhmadHasta/data-usage-monitor.git
cd data-usage-monitor

# Run Unit Tests
./gradlew testDebugUnitTest

# Assemble Debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Quick Settings Tile Setup

1. **Android 13+ (API 33+)**:
   - Open the app.
   - Tap the **"Add to Quick Settings"** prompt button on the top banner.
   - Confirm the system dialog.

2. **Manual Setup (All Android versions)**:
   - Swipe down twice from the top of the screen to open the full Quick Settings panel.
   - Tap the **Edit (pencil)** button.
   - Scroll down to find the **"Data Usage"** tile.
   - Drag and place it into your active Quick Settings tiles.

---

## 🧪 Testing

The repository contains unit tests covering:
- **`ByteFormatterTest`**: Exact boundary conversions for Bytes, KB, MB, GB, and TB.
- **`UsagePeriodTest`**: Start/end time range calculations for Today, This Week, and This Month across local timezones.
- **`AppDataUsageTest`**: Consumption aggregations and sorting algorithms.

Run the test suite:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 License

```text
Copyright (c) 2026 Ahmad Hasta

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```