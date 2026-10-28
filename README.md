# Warrio Mobile App

Warrio is an Android application for managing products, receipts, warranties,
claims, repairs, and after-sales service records.

## Current foundation

- Kotlin and Jetpack Compose
- Material 3
- Navigation Compose
- Minimum Android version: API 24
- Compile SDK: API 37

The current UI is a small application shell with a mock Login-to-Dashboard flow.
Authentication is deliberately local at this stage; Firebase integration will be
added as a separate, tested task.

## Local setup

1. Install Android Studio and Android SDK Platform 37.
2. Open this repository in Android Studio.
3. Allow Gradle sync to complete.
4. Run the `app` configuration on an emulator or Android device.

Command-line verification on Windows:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

Do not commit `local.properties`, Firebase credentials, signing keys, or other
machine-specific and sensitive files.
