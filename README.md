# Warrio Mobile App

Warrio is an Android application for managing products, receipts, warranties,
claims, repairs, and after-sales service records.

## Current foundation

- Kotlin and Jetpack Compose
- Material 3
- Navigation Compose
- Minimum Android version: API 24
- Compile SDK: API 37

The current UI includes login and registration forms, validation, a customer
dashboard, bottom navigation, and an empty product-wallet state. Authentication
uses a clearly marked local fake repository; Firebase and product persistence
will be added as separate, tested tasks.

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
