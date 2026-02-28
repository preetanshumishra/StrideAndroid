# Stride Android

Native Android application for smart errand management and personal place saving, powered by the Woosmap Geofencing SDK for automatic visit detection and geofence alerts. Built with Kotlin, Jetpack Compose, and modern Android development practices.

## Overview

Stride Android is a native Android app providing intelligent errand and place management capabilities. The app follows the MVVM architecture pattern with Dagger for dependency injection and Jetpack Compose for modern declarative UI. It integrates the Woosmap Geofencing SDK to passively detect when you visit a saved place and to alert you when you're nearby with pending errands.

## Tech Stack

- **Language:** Kotlin 2.2.20
- **UI Framework:** Jetpack Compose 1.8.1
- **Material Design:** Material3 1.3.1
- **Min SDK:** API 28 (Android 9.0)
- **Target SDK:** API 36 (Android 15)
- **Dependency Injection:** Dagger 2.59.1
- **Networking:** Retrofit 2.11.0 + OkHttp 4.12.0
- **Storage:** DataStore 1.2.0
- **Location SDK:** Woosmap Geofencing SDK — visit detection + custom geofencing
- **Notifications:** NotificationCompat — local push alerts on geofence entry
- **Build System:** Gradle 8.13

## Project Structure

```
StrideAndroid/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/preetanshumishra/stride/
│   │   │   │   ├── StrideApplication.kt          # App init, Dagger, notification channel
│   │   │   │   ├── MainActivity.kt               # Woosmap onResume/onPause, notification permission
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   └── TokenManager.kt
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   ├── Place.kt
│   │   │   │   │   │   ├── Errand.kt
│   │   │   │   │   │   ├── NearbyResponse.kt
│   │   │   │   │   │   └── PlaceCollection.kt
│   │   │   │   │   └── network/
│   │   │   │   │       ├── ApiService.kt
│   │   │   │   │       ├── TokenInterceptor.kt
│   │   │   │   │       └── TokenAuthenticator.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── AppModule.kt              # Retrofit, OkHttp, WoosmapManager
│   │   │   │   │   ├── ServiceModule.kt          # All app services
│   │   │   │   │   ├── AppComponent.kt           # Dagger component
│   │   │   │   │   └── AppDependencies.kt        # Global dependency accessor
│   │   │   │   ├── services/
│   │   │   │   │   ├── AuthService.kt
│   │   │   │   │   ├── PlaceService.kt
│   │   │   │   │   ├── ErrandService.kt
│   │   │   │   │   ├── NearbyService.kt
│   │   │   │   │   ├── RouteService.kt
│   │   │   │   │   └── CollectionService.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── PlacesScreen.kt
│   │   │   │   │   │   ├── ErrandsScreen.kt
│   │   │   │   │   │   ├── SmartRouteScreen.kt
│   │   │   │   │   │   ├── NearbyScreen.kt
│   │   │   │   │   │   ├── CollectionsScreen.kt
│   │   │   │   │   │   └── SettingsScreen.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── NavigationCard.kt
│   │   │   │   │   │   ├── PlaceCard.kt
│   │   │   │   │   │   └── ErrandCard.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   └── Theme.kt
│   │   │   │   │   └── navigation/
│   │   │   │   │       └── NavGraph.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Resource.kt
│   │   │   │   │   ├── LocationHelper.kt
│   │   │   │   │   ├── WoosmapManager.kt         # Visit detection + geofence alerts
│   │   │   │   │   └── AuthResponseExtensions.kt
│   │   │   │   └── viewmodel/
│   │   │   │       ├── LoginViewModel.kt
│   │   │   │       ├── RegisterViewModel.kt
│   │   │   │       ├── HomeViewModel.kt
│   │   │   │       ├── PlacesViewModel.kt        # Calls registerGeofences after load
│   │   │   │       ├── ErrandsViewModel.kt
│   │   │   │       ├── SmartRouteViewModel.kt
│   │   │   │       ├── NearbyViewModel.kt
│   │   │   │       ├── CollectionsViewModel.kt
│   │   │   │       ├── SettingsViewModel.kt
│   │   │   │       └── ViewModelFactory.kt
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   ├── androidTest/
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew & gradlew.bat
└── .gitignore
```

## Setup Instructions

### Prerequisites

- macOS, Windows, or Linux
- Android Studio 2024.1 or later
- Android SDK 36
- JDK 17 or later
- Gradle 8.13 or later

### Installation

1. **Clone and navigate to project:**
   ```bash
   cd StrideAndroid
   ```

2. **Sync Gradle dependencies:**
   - Open project in Android Studio — it will auto-sync
   - Or run: `./gradlew build`

3. **Select target and run:**
   - Select "app" run configuration
   - Choose Android Emulator (API 28+) or physical device
   - Press Shift+F10 (Windows/Linux) or Ctrl+R (macOS)

## Build & Run

### Using Android Studio
1. Open project in Android Studio
2. Select "app" configuration
3. Click Run (Shift+F10 or Ctrl+R)

### Using Command Line

```bash
./gradlew build              # Build debug and release
./gradlew assembleDebug      # Build debug APK
./gradlew assembleRelease    # Build release APK
./gradlew installDebug       # Install on connected device/emulator
./gradlew test               # Run unit tests
./gradlew clean              # Clean build outputs
```

## Architecture

### MVVM Pattern
- **Views:** Jetpack Compose UI screens
- **ViewModels:** State management with `StateFlow`
- **Models:** Data classes for API responses

### Dependency Injection
Uses Dagger for managing dependencies:
- **`AppComponent`:** Central Dagger component defining the dependency graph
- **`AppDependencies`:** Accessor for all singletons throughout the app
- **`ViewModelFactory`:** Creates ViewModels with injected dependencies
- **`AppModule`:** Provides Retrofit, OkHttp, `WoosmapManager`
- **`ServiceModule`:** Provides all app services (AuthService, PlaceService, etc.)

### Woosmap Integration
`WoosmapManager` is a Dagger `@Singleton` provided in `AppModule`, accessible via `appDependencies.woosmapManager`:
- **Visit detection:** `VisitReadyCallback` → Haversine match against saved places (100m threshold) → `PATCH /api/v1/places/:id/visit`
- **Geofencing:** Circular geofences (100m radius) registered per saved place; `RegionLogReadyCallback(didEnter=true)` → `POST /api/v1/nearby` → `NotificationCompat` local notification
- **Tracking lifecycle:** `onResume()` / `onPause()` called in `MainActivity`
- **Geofence registration:** `PlacesViewModel.loadPlaces()` calls `registerGeofences(places)` on every load
- **API key:** `""` (visit detection and custom geofencing work without a Woosmap store key)
- **Notification channel:** `stride_geofence` (IMPORTANCE_HIGH) created in `StrideApplication.onCreate()`

### Data Flow
1. **UI screens** trigger actions on **ViewModels**
2. **ViewModels** manage `StateFlow` for state
3. **Services** handle business logic and API calls via Retrofit
4. **TokenManager** handles secure token storage (DataStore)
5. **WoosmapManager** runs passively in the background for location events
6. **State updates** automatically recompose UI

## Dependencies

Key libraries:
- **Woosmap Geofencing SDK** — `com.webgeoservices.woosmapgeofencing:woosmap-mobile-sdk:+`
- **Dagger** — dependency injection with code generation
- **Retrofit + OkHttp** — REST API client
- **Jetpack Compose + Material3** — modern declarative UI
- **Coroutines + Flow** — async programming and reactive state
- **DataStore** — secure preferences storage
- **Google Play Services Location** — fused location provider

## Configuration

### Environment
Update backend API URL in `AppModule.kt`:
```kotlin
private const val BASE_URL = "https://strideapi-1048111785674.us-central1.run.app/" // Production
// or
private const val BASE_URL = "http://10.0.2.2:5001/" // Emulator (local dev)
```

### Permissions
Declared in `AndroidManifest.xml`:
- `INTERNET` — API calls
- `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` — location features
- `ACCESS_BACKGROUND_LOCATION` — Woosmap background geofencing
- `POST_NOTIFICATIONS` — geofence alert notifications (Android 13+, requested at runtime)

## Features

- ✅ User Authentication (Login/Register)
- ✅ Secure Token Storage (DataStore)
- ✅ Place Management (CRUD, collections, tags)
- ✅ Errand Management (CRUD, priorities, deadlines)
- ✅ Smart Errand Routing
- ✅ Nearby Places & Errands
- ✅ Collections Management
- ✅ Settings (profile, password, account deletion)
- ✅ Visit Detection (Woosmap SDK — auto-records visits when you spend time at a saved place)
- ✅ Geofence Alerts (Woosmap SDK — local push notification with pending errands when near a saved place)

## Dependency Injection Setup

### How Dagger Works in Stride Android

1. **Application Initialization** (`StrideApplication.kt`):
   - Builds `DaggerAppComponent` on app startup
   - Creates `AppDependencies` container
   - Creates `stride_geofence` notification channel

2. **Service Provision** (Modules):
   - `AppModule.kt`: Provides Retrofit, OkHttp, `LocationHelper`, `WoosmapManager`
   - `ServiceModule.kt`: Provides all services (Auth, Place, Errand, Nearby, Route, Collection)
   - All instances are `@Singleton`

3. **ViewModel Creation** (`ViewModelFactory.kt`):
   - Injects dependencies via `appDependencies`
   - Used in Compose screens via `ViewModelProvider`

4. **Accessing Dependencies**:
   ```kotlin
   // In Activity
   val authService = appDependencies.authService

   // In ViewModel
   appDependencies.woosmapManager.registerGeofences(places)
   ```

### Adding a New Dependency

1. Add provider method in `AppModule.kt` or `ServiceModule.kt`:
   ```kotlin
   @Provides
   @Singleton
   fun provideMyService(apiService: ApiService): MyService = MyService(apiService)
   ```

2. Add to `AppComponent.kt`:
   ```kotlin
   val myService: MyService
   ```

3. Add to `AppDependencies.kt`:
   ```kotlin
   val myService: MyService = appComponent.myService
   ```

## Troubleshooting

### Build Fails
```bash
./gradlew clean
./gradlew build --stacktrace
rm -rf .gradle build app/build && ./gradlew build
```

### Gradle Sync Issues
In Android Studio: File → Invalidate Caches → Invalidate and Restart

### Emulator Issues
```bash
emulator -list-avds
emulator -avd <emulator_name>
emulator -avd <emulator_name> -wipe-data
```

### API Connection Issues
- Check emulator can reach host: `adb shell ping 10.0.2.2`
- For physical device, use actual IP address in `AppModule.BASE_URL`

## Stride Ecosystem

This project is part of the Stride smart errand and place management ecosystem:

- **[StrideAPI](https://github.com/preetanshumishra/StrideAPI)** - Node.js/Express backend API with MongoDB
- **[StrideiOS](https://github.com/preetanshumishra/StrideiOS)** - Native iOS app (Swift 6 + SwiftUI)

## License

MIT

## Author

Preetanshu Mishra
