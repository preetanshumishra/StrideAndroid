# Stride Android

Native Android application for smart errand management and personal place saving. Built with Kotlin, Jetpack Compose, and modern Android development practices.

## Overview

Stride Android is a native Android app providing intelligent errand and place management capabilities. The app follows the MVVM architecture pattern with Dagger for dependency injection and Jetpack Compose for modern declarative UI.

## Tech Stack

- **Language:** Kotlin 2.2.20
- **UI Framework:** Jetpack Compose 1.8.1
- **Material Design:** Material3 1.3.1
- **Min SDK:** API 28 (Android 9.0)
- **Target SDK:** API 36 (Android 15)
- **Dependency Injection:** Dagger 2.59.1
- **Networking:** Retrofit 2.11.0 + OkHttp 4.12.0
- **Storage:** DataStore 1.2.0
- **Build System:** Gradle 8.13

## Project Structure

```
StrideAndroid/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/preetanshumishra/stride/
│   │   │   │   ├── StrideApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   └── TokenManager.kt
│   │   │   │   │   ├── models/
│   │   │   │   │   │   ├── User.kt
│   │   │   │   │   │   ├── Place.kt
│   │   │   │   │   │   ├── Errand.kt
│   │   │   │   │   │   └── PlaceCollection.kt
│   │   │   │   │   └── network/
│   │   │   │   │       ├── ApiService.kt
│   │   │   │   │       └── TokenInterceptor.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── AppModule.kt
│   │   │   │   │   ├── ServiceModule.kt
│   │   │   │   │   ├── AppComponent.kt
│   │   │   │   │   └── AppDependencies.kt
│   │   │   │   ├── services/
│   │   │   │   │   ├── AuthService.kt
│   │   │   │   │   ├── PlaceService.kt
│   │   │   │   │   └── ErrandService.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── LoginScreen.kt
│   │   │   │   │   │   ├── RegisterScreen.kt
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── PlacesScreen.kt
│   │   │   │   │   │   └── ErrandsScreen.kt
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
│   │   │   │   │   └── AuthResponseExtensions.kt
│   │   │   │   └── viewmodel/
│   │   │   │       ├── LoginViewModel.kt
│   │   │   │       ├── RegisterViewModel.kt
│   │   │   │       ├── HomeViewModel.kt
│   │   │   │       ├── PlacesViewModel.kt
│   │   │   │       ├── ErrandsViewModel.kt
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
   - Open project in Android Studio
   - Android Studio will auto-sync dependencies
   - Or run: `./gradlew build`

3. **Select target:**
   - Select "app" run configuration
   - Choose Android Emulator or physical device

4. **Run application:**
   - Press Shift+F10 (Windows/Linux) or Ctrl+R (macOS)
   - Or click Run button in Android Studio

## Build & Run

### Using Android Studio
1. Open project in Android Studio
2. Select "app" configuration
3. Click Run (Shift+F10 or Ctrl+R)

### Using Command Line

**Build Debug APK:**
```bash
./gradlew build
```

**Build Release APK:**
```bash
./gradlew assembleRelease
```

**Run on Emulator:**
```bash
./gradlew installDebug
./gradlew runDebug
```

**Build and Run:**
```bash
./gradlew build installDebug
```

## Architecture

### MVVM Pattern
- **Views:** Jetpack Compose UI screens
- **ViewModels:** State management with StateFlow
- **Models:** Data classes for API responses and local storage

### Dependency Injection
Uses Dagger for managing dependencies:
- **AppComponent:** Central Dagger component that defines the dependency graph
- **AppDependencies:** Container for accessing all singletons throughout the app
- **ViewModelFactory:** Creates ViewModels with injected dependencies
- **Modules:** AppModule and ServiceModule provide dependency instances
- Constructor injection for all services and ViewModels

### Data Flow
1. **UI screens** trigger actions on **ViewModels**
2. **ViewModels** manage **StateFlow** for state
3. **Services** handle business logic
4. **ApiClient** manages API calls via Retrofit
5. **TokenManager** handles secure token storage
6. **State updates** automatically recompose UI

## Dependencies

Key libraries:
- **Dagger:** Dependency injection framework with code generation
- **Retrofit:** REST API client
- **OkHttp:** HTTP client with logging
- **Compose:** Modern declarative UI
- **Coroutines:** Async programming and Flow for reactive state
- **DataStore:** Secure preferences storage

Install/Update with:
```bash
./gradlew build
```

## Dependency Injection Setup

### How Dagger Works in Stride Android

1. **Application Initialization** (`StrideApplication.kt`):
   - Builds `DaggerAppComponent` on app startup
   - Creates `AppDependencies` container
   - Makes dependencies globally accessible

2. **Service Provision** (Modules):
   - `AppModule.kt`: Provides Retrofit, OkHttp, Gson
   - `ServiceModule.kt`: Provides AuthService, PlaceService, ErrandService
   - All services are cached as singletons

3. **ViewModel Creation** (`ViewModelFactory.kt`):
   - Injects dependencies into ViewModels
   - Used in Compose screens via `remember`

4. **Accessing Dependencies**:
   ```kotlin
   // In Activity
   val authService = appDependencies.authService

   // In Compose
   val viewModel = remember {
       val owner = LocalViewModelStoreOwner.current ?: error("No owner")
       ViewModelProvider(owner.viewModelStore, ViewModelFactory())
           .get(LoginViewModel::class.java)
   }
   ```

### Adding a New Dependency

1. Add provider method in appropriate module:
   ```kotlin
   @Provides
   @Singleton
   fun provideMyService(apiService: ApiService): MyService {
       return MyService(apiService)
   }
   ```

2. Add to `AppComponent.kt`:
   ```kotlin
   val myService: MyService
   ```

3. Add to `AppDependencies.kt`:
   ```kotlin
   val myService: MyService = appComponent.myService
   ```

4. Use in ViewModel or Activity via `appDependencies.myService`

## Configuration

### Environment
Update backend API URL in `AppModule.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:5001" // Emulator
// or
private const val BASE_URL = "http://localhost:5001" // Physical device
```

For production:
```kotlin
private const val BASE_URL = "https://your-stride-api.com"
```

### Build Configuration
Minimum SDK is configured in `app/build.gradle.kts`:
```kotlin
minSdk = 28
targetSdk = 36
```

## Features

- ✅ User Authentication (Login/Register)
- ✅ Secure Token Storage (DataStore)
- ✅ API Integration (Retrofit with OkHttp)
- ✅ Jetpack Compose UI with Material3 (green/teal theme)
- ✅ MVVM Architecture with ViewModels
- ✅ Dagger Dependency Injection
- ✅ Coroutines & Flow for reactive state
- ✅ Place Management (view, delete)
- ✅ Errand Management (view, complete, delete)
- ✅ Android 9.0+ Compatibility

## Development Guidelines

### Code Style
- Use Kotlin naming conventions
- Follow Jetpack Compose best practices
- Keep composables pure and side-effect-free
- Use StateFlow for state management

### Adding Dependencies
1. Add dependency to `app/build.gradle.kts`
2. Run `./gradlew build` to sync
3. If adding a new service, add provider method to AppModule or ServiceModule

### Testing
- Unit tests in `app/src/test/`
- Instrumented tests in `app/src/androidTest/`
- Mock services for testing ViewModels

## Troubleshooting

### Build Fails
```bash
# Clean build
./gradlew clean

# Rebuild with verbose output
./gradlew build --stacktrace

# Clear cache
rm -rf .gradle build app/build
./gradlew build
```

### Gradle Sync Issues
```bash
# Invalidate caches in Android Studio:
# File → Invalidate Caches... → Invalidate and Restart

# Or clean and rebuild
./gradlew clean build
```

### Emulator Issues
```bash
# List available emulators
emulator -list-avds

# Launch specific emulator
emulator -avd <emulator_name>

# Wipe emulator data
emulator -avd <emulator_name> -wipe-data
```

### API Connection Issues
- Verify backend is running on correct port
- Check emulator can reach host: `adb shell ping 10.0.2.2`
- For physical device, use actual IP address
- Check firewall settings

## Gradle Commands

```bash
# Build
./gradlew build                 # Build debug and release
./gradlew assembleDebug        # Build debug APK
./gradlew assembleRelease      # Build release APK

# Testing
./gradlew test                 # Run unit tests
./gradlew connectedAndroidTest # Run instrumented tests

# Cleaning
./gradlew clean                # Clean build outputs
./gradlew cleanBuildCache      # Clean build cache

# Development
./gradlew installDebug         # Install debug APK
./gradlew tasks                # List available tasks
```

## Stride Ecosystem

This project is part of the Stride smart errand and place management ecosystem:

- **[StrideAPI](https://github.com/preetanshumishra/StrideAPI)** - Node.js/Express backend API with MongoDB
- **[StrideiOS](https://github.com/preetanshumishra/StrideiOS)** - Native iOS app (Swift + SwiftUI)

## License

MIT

## Author

Preetanshu Mishra
