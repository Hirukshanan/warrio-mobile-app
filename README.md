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

Manual product registration is available as an in-memory development flow. It
validates required product and warranty details, labels customer-entered items
as `SELF_ADDED`, and keeps them only for the current app session until the team
agrees on the shared cloud product contract.

## Receipt text extraction

Open the wallet, choose **Scan receipt**, select one image, then tap
**Extract text**. The bundled Latin-script ML Kit recognizer displays selectable
raw text for review, with processing, no-text-found, failure, and retry states.
Changing the image clears the previous result; cancelling the picker preserves
it. Leaving the screen cancels its UI work. Extraction results are temporary
and reset on screen recreation; the selected image URI is restored on rotation.

Image loading and recognition wait off the UI thread. The display preview is
sampled to a longest edge of at most 1600 pixels; OCR uses a separate input capped
at 2400 pixels to retain more text detail. Photo orientation is applied before
recognition. Receipt contents are not logged or sent to a project backend.
Product-field parsing, shared Product mapping, confirmation, and Firebase saving
are subsequent integration steps with the product-details feature.

Manual checks on a device:

1. Select a readable Latin-script receipt and extract text; compare it to the photo.
2. Select a photo without text and confirm the helpful empty-result message.
3. Cancel the picker after extraction and confirm the image and result remain.
4. Select another image while processing; confirm no old result appears for it.
5. Try a large or rotated photo and an unreadable image; verify retry/reselection.
6. Return to the wallet, and confirm manual product registration still works.

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
