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

Wallet product cards open a product-details screen that shows product,
purchase, warranty, and verification information. Optional model and serial
values are handled explicitly when customers do not provide them.

## Receipt text extraction

Open the wallet, choose **Scan receipt**, select one image, then tap
**Extract text**. The bundled Latin-script ML Kit recognizer displays selectable
editable raw text for review, with processing, success, no-text-found, failure,
and retry states. Customers can correct, replace, or clear the receipt text;
an empty or failed extraction also allows manual text entry. Re-extraction
replaces any edits, as stated next to the editor. **Review product details**
uses the edited text to suggest fields in the existing registration form.
Review or correct the suggestions, fill in missing fields, then tap **Add to
wallet** to save one `SELF_ADDED` product for the current app session.
Changing the image clears the previous result; cancelling the picker preserves
it. Leaving the screen cancels its UI work. Extraction results are temporary
and reset on screen recreation; the selected image URI is restored on rotation.

Image loading and recognition wait off the UI thread. The display preview is
sampled to a longest edge of at most 1600 pixels; OCR uses a separate input capped
at 2400 pixels to retain more text detail. Photo orientation is applied before
recognition. Receipt contents are not logged or sent to a project backend.
Prefill uses clearly labelled values such as `Product:`, `Brand:`,
`Purchase date:` (ISO format), and `Warranty: 12 months`. Ambiguous or
unusable values are left blank. Firebase persistence is a follow-up step.

Manual checks on a device:

1. Select a readable Latin-script receipt and extract text; compare it to the photo,
   then correct and clear text in the editor.
2. Select a photo without text and confirm the empty-result message and manual editor.
3. Cancel the picker after editing and confirm the image and corrected text remain.
4. Select another image while processing; confirm no old result appears for it.
5. Try a large or rotated photo and an unreadable image; verify retry/reselection.
6. Return to the wallet, register a product, and tap its card to verify details
   navigation; confirm Scan receipt is still available in the populated wallet.
7. Correct the receipt text, open the review form, and go back to confirm the
   text is still there. Open the form again, finish the required fields, and tap
   **Add to wallet**. Open the new product and verify the reviewed values.

`ReceiptOcrStateTest` uses synthetic recognition responses to cover loading,
success, empty text, editable results, failure/retry, duplicate suppression, and
cancellation. These unit tests do not validate the native model's OCR accuracy;
the device checks above are still required.

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

## CI and debug delivery

`.github/workflows/android-ci.yml` runs on branch pushes, pull requests, and
manual dispatches. It uses Ubuntu 24.04, Temurin JDK 21, the checked-in Gradle
wrapper, Android SDK Platform 37, and Build Tools 36.0.0. It runs, in order:

1. `:app:testDebugUnitTest`
2. `:app:lintDebug`
3. `:app:assembleDebug`

The pipeline caches Gradle dependencies, cancels superseded runs, and uses a
2 GiB daemon heap. The local untracked `gradle/gradle-daemon-jvm.properties`
is not needed by CI; JDK 21 is selected by the workflow. App Java source and
target compatibility remain at 17.

After the workflow is committed and pushed, open **Actions → Android CI → a run**.
Download `warrio-debug-<commit SHA>` under **Artifacts** for the debug APK.
Test and lint reports are retained for seven days, including reports from failed
runs when available. APK delivery happens only after all verification steps pass.
Manual runs require the workflow to exist on the repository's default branch.

This delivers a debug APK for testing. It does not publish to Google Play or
Firebase App Distribution. Production delivery requires a chosen destination,
release signing, and appropriate repository secrets/environment protection.
Debug builds use the runner's temporary debug signing key, so APKs from different
runs may require uninstalling the previous build before installation.

The workflow needs no project secrets and grants only `contents: read`.
Uploads contain only the APK and explicit test/lint reports; build scans, session
traces, source archives, signing keys, and local configuration are not uploaded.
GitHub-hosted runs process the code already pushed to GitHub. Keep credentials
and private receipt fixtures out of the repository and generated reports.

Action pins were verified against their public tags on 2026-11-16:

| Action documentation and version | Pinned commit |
| --- | --- |
| [actions/checkout v4.2.2](https://github.com/actions/checkout/tree/v4.2.2) | `11bd71901bbe5b1630ceea73d27597364c9af683` |
| [actions/setup-java v4.7.1](https://github.com/actions/setup-java/tree/v4.7.1) | `c5195efecf7bdfc987ee8bae7a71cb8b11521c00` |
| [actions/upload-artifact v4.6.2](https://github.com/actions/upload-artifact/tree/v4.6.2) | `ea165f8d65b6e75b540449e92b4886f43607fa02` |
