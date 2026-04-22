# MyRemote Admin - Android App

Remote administration system for controlling Android devices from a web browser.

## Quick Start

### Build APK using GitHub Actions

1. **Push this code to your GitHub repository**
   ```bash
   git init
   git add .
   git commit -m "Initial commit: MyRemote Admin"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
   git push -u origin main
   ```

2. **Go to Actions tab** in your GitHub repository

3. **Enable workflows** if prompted

4. **Run the build**:
   - Workflow runs automatically on push to main/master
   - Or manually click "Run workflow" button

5. **Download APK** from the workflow artifacts section

## Project Structure

```
myremote-android/
├── .github/workflows/
│   ├── build.yml              # Debug + Unsigned Release builds
│   └── build-signed.yml       # Signed release builds (manual)
├── app/
│   ├── src/main/
│   │   ├── java/com/myremote/admin/
│   │   │   ├── MyRemoteApplication.kt
│   │   │   ├── ui/MainActivity.kt
│   │   │   ├── wizard/         # 9-screen setup wizard
│   │   │   ├── data/           # Models, secure storage
│   │   │   └── service/        # FCM service, accessibility
│   │   ├── res/               # UI resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/wrapper/
├── build.gradle.kts
├── gradlew                    # Gradle wrapper script
└── SETUP_GITHUB_ACTIONS.md    # Detailed setup guide
```

## Features

- **9-Screen Setup Wizard**: Complete permission configuration
- **Secure Device Key**: 32-character unique key per device
- **FCM Command Handling**: Remote commands via Firebase
- **Accessibility Service**: Gesture control (tap, swipe, type)
- **Screen Sharing**: WebRTC-based live view
- **Foreground Service**: Persistent background operation
- **Boot Persistence**: Auto-start on device reboot

## Commands Supported

| Command | Description |
|---------|-------------|
| `ping` | Check device connectivity |
| `location` | Get current GPS location |
| `photo` | Take photo with camera |
| `audio` | Record audio |
| `screen_share` | Start WebRTC screen sharing |
| `tap` | Tap at coordinates |
| `swipe` | Swipe gesture |
| `type` | Type text |
| `toggle_wifi` | Toggle WiFi |
| `toggle_bluetooth` | Toggle Bluetooth |

## Configuration Required

### 1. Firebase Setup
- Create Firebase project at https://console.firebase.google.com
- Add Android app with package: `com.myremote.admin`
- Download `google-services.json`
- Add as `GOOGLE_SERVICES_JSON` secret in GitHub (optional)

### 2. Signing (for production releases)
Generate keystore:
```bash
keytool -genkey -v -keystore myremote-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias myremote
```

Add these GitHub secrets:
- `RELEASE_KEYSTORE_BASE64`: `base64 -w0 myremote-release.jks`
- `KEYSTORE_PASSWORD`: Your keystore password
- `KEY_ALIAS`: Your key alias
- `KEY_PASSWORD`: Your key password

## Building Locally

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (unsigned)
./gradlew assembleRelease

# Install on connected device
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Requirements

- **Minimum Android**: 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)
- **JDK**: 17
- **Kotlin**: 1.9.0

## Security Notes

⚠️ **Important**: This app grants powerful remote control capabilities. Use responsibly:
- Only install on devices you own
- Keep the device key secret
- The app requires extensive permissions for functionality
- Review all permissions during setup

## License

MIT License - See LICENSE file for details

## Support

For issues and questions, please open an issue on GitHub.
