# MyRemote Admin - Remote Android Administration System

A comprehensive remote administration system for Android devices with a web-based console.

## Project Structure

```
/workspace
├── android-app/          # Android client application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/myremote/admin/
│   │   │   │   ├── service/           # Background services
│   │   │   │   ├── wizard/            # Setup wizard activities
│   │   │   │   ├── ui/                # Main UI activities
│   │   │   │   ├── util/              # Utility classes
│   │   │   │   └── model/             # Data models
│   │   │   └── res/                   # Android resources
│   │   └── build.gradle
│   └── build.gradle
└── web-console/          # Next.js web administration console
    └── app/
        ├── api/         # API routes
        ├── dashboard/   # Dashboard pages
        └── lib/         # Firebase configuration
```

## Features

### Android App
- **Secure Device Key Generation**: 32-character unique identifier
- **Encrypted Storage**: Using Android Keystore and EncryptedSharedPreferences
- **Accessibility Service**: For remote gesture control (tap, swipe, etc.)
- **Foreground Service**: Persistent background operation
- **Firebase Cloud Messaging**: Real-time command delivery
- **Setup Wizard**: Guided permission and configuration process

### Web Console
- **Google Authentication**: Secure user login
- **Device Management**: Add and manage multiple devices
- **Remote Commands**: Send commands to connected devices
- **Real-time Status**: Monitor device connectivity

## Supported Commands

| Command | Description |
|---------|-------------|
| PING | Check device status |
| GET_LOCATION | Retrieve device location |
| TAP | Perform tap at coordinates |
| SWIPE | Perform swipe gesture |
| TYPE_TEXT | Type text input |
| PRESS_BACK/HOME | Navigation commands |
| TOGGLE_WIFI | Toggle WiFi |
| GET_DEVICE_INFO | Get device information |
| TAKE_PHOTO | Capture photo |
| RECORD_AUDIO | Record audio |
| START/STOP_SCREEN_SHARE | WebRTC screen sharing |

## Setup Instructions

### Prerequisites
1. Firebase project with:
   - Firestore Database
   - Cloud Messaging (FCM)
   - Authentication (Google Sign-In)

2. For Android:
   - Android Studio Arctic Fox or later
   - JDK 17
   - google-services.json in app/ directory

3. For Web Console:
   - Node.js 18+
   - Firebase Admin SDK service account

### Android Setup
1. Download `google-services.json` from Firebase Console
2. Place it in `android-app/app/`
3. Open project in Android Studio
4. Build and install on target device

### Web Console Setup
```bash
cd web-console
npm install
cp .env.example .env.local
# Edit .env.local with your Firebase credentials
npm run dev
```

### Environment Variables

**Web Console (.env.local)**:
```
NEXT_PUBLIC_FIREBASE_API_KEY=your_api_key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your_auth_domain
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your_project_id
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your_storage_bucket
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
FIREBASE_SERVICE_ACCOUNT={"type":"service_account",...}
```

## Security Considerations

- All device keys are stored using Android's EncryptedSharedPreferences
- Commands expire after 5 minutes if not executed
- Device key verification required for all commands
- Firebase security rules should restrict access to authorized users only

## Architecture

```
┌─────────────┐     FCM      ┌─────────────┐
│   Web       │ ───────────► │   Android   │
│  Console    │              │    Device   │
│             │ ◄─────────── │             │
└─────────────┘   Firestore  └─────────────┘
                     ▲
                     │
              Command Results
```

## License

MIT License - See LICENSE file for details

## Disclaimer

This tool is intended for legitimate remote administration purposes only. 
Ensure you have proper authorization before installing on any device.
