BUILD PERSONAL REMOTE ADMIN - ANDROID APP + VERCEL WEB CONSOLE

PROJECT GOAL
Build a remote administration system for one user to control their own Android phone from a web browser. Everything must be visible on the phone. No hidden background actions. All permissions are granted manually by the owner on first launch.

SECURITY MODEL - DEVICE KEY
1. Each Android device generates a unique 32-character key on first launch. Format: uppercase letters and numbers only. Example: 7F3K9P2M...
2. In Android app Settings screen, show this key with Copy button and Regenerate button.
3. User must manually type or paste this exact key into the Vercel web UI under Devices > Add Device.
4. Web UI stores the key in Firestore under users/{uid}/devices/{deviceId}.key
5. Every command sent from web to phone must include this key in the FCM data payload. Android app must reject any command where the key does not match the locally stored key. Log rejection.
6. If keys do not match, show error in web UI: "Key mismatch".

ANDROID APP - DETAILED TASKS

A. Project setup
- Package name: com.myremote.admin
- minSdk 30, targetSdk 34
- Dependencies: Firebase BoM, WebRTC, WorkManager

B. First-run wizard (SetupActivity)
- Create 9 sequential screens. Do not allow skip. Next button disabled until check passes.
- Screen 1: Runtime permissions. Request CAMERA, RECORD_AUDIO, ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION, READ_SMS, SEND_SMS, READ_CALL_LOG, READ_CONTACTS, POST_NOTIFICATIONS, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO. After each grant, call checkSelfPermission. If denied, stay on screen.
- Screen 2: Accessibility. Button opens Settings.ACTION_ACCESSIBILITY_SETTINGS. On resume, check if your service is in enabled services list. If not, stay.
- Screen 3: Notification access. Open Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS. Check via NotificationManagerCompat.getEnabledListenerPackages.
- Screen 4: All files access. Open Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION. Check Environment.isExternalStorageManager().
- Screen 5: Battery optimization. Open Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS. Check PowerManager.isIgnoringBatteryOptimizations().
- Screen 6: Device Admin optional. Open DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN. Check isAdminActive().
- Screen 7: Generate device key. Call generateKey(). Show key, Copy button. Save to EncryptedSharedPreferences.
- Screen 8: OEM whitelist guide. Show images for Xiaomi, Samsung, OnePlus. Require checkbox "I have enabled autostart".
- Screen 9: Finish. Start ForegroundService.

C. Boot persistence
- Add RECEIVE_BOOT_COMPLETED permission.
- Create BootReceiver that listens for BOOT_COMPLETED and LOCKED_BOOT_COMPLETED.
- In onReceive, call context.startForegroundService(new Intent(context, RemoteService.class)).
- RemoteService must call startForeground within 8 seconds with notification channel "Remote admin active".

D. Core service (RemoteService)
- Maintain Firebase Messaging listener.
- On message received: parse JSON, verify deviceKey matches stored key, if not return error.
- Implement actions:
    * ping -> reply pong
    * get_location -> use FusedLocationProvider, return lat,lng
    * take_photo -> open camera, capture, upload to Firebase Storage, return URL
    * record_audio_10s -> MediaRecorder, 10 seconds, upload
    * start_screen_share -> launch MediaProjection intent. IMPORTANT: This shows system picker. User must tap Allow. If user denies, return error. Stream via WebRTC.
    * stop_screen_share -> stop projection
    * tap x y -> AccessibilityService.dispatchGesture with 100ms tap
    * swipe x1 y1 x2 y2 -> dispatchGesture
    * type_text -> find focused node, perform ACTION_SET_TEXT
    * toggle_wifi, toggle_bluetooth -> launch Settings Panel
    * airplane_mode_toggle, mobile_data_toggle, gps_toggle -> use Accessibility to open Settings and click switch visibly

E. AccessibilityService (MyAccessibilityService)
- Declare in manifest with proper config.
- Implement onAccessibilityEvent empty, implement performTap, performSwipe methods called by RemoteService via binder.

F. Data storage
- Use EncryptedSharedPreferences for deviceKey.
- Firestore collection: devices/{deviceId} with fields: lastSeen, battery, model, keyHash.

VERCEL WEBAPP - DETAILED TASKS

A. Setup
- Next.js 14 app router. Deploy to Vercel.
- Install firebase-admin. Store service account in Vercel env vars.

B. Authentication
- Simple email/password login via Firebase Auth. No signup page needed.

C. Firestore structure
- users/{uid}/devices/{deviceId}:
    - name: string
    - key: string (the 32-char key user pasted)
    - createdAt: timestamp
    - lastCommand: string

D. Pages
1. /devices - List devices. Button Add Device opens modal with fields: Device Name, Device Key. Save to Firestore.
2. /devices/[id] - Dashboard with tabs: Live View, Control, Files, Location, Logs.
3. Live View tab - WebRTC video element. Button Start View sends FCM command start_screen_share with key. Show message "Approve screen share on phone".
4. Control tab - D-pad for swipe, text box for typing, buttons for each toggle. Each button calls POST /api/command with {deviceId, action, params, key}.
5. Files tab - Button list_files, show results.
6. Location tab - Show map with last location.

E. API routes
- /api/command - receives deviceId, action, key. Verify key matches Firestore. Send FCM data message to topic deviceId. Return requestId.
- /api/webhook/result - Android posts results here with requestId, store in Firestore.

F. Security checks
- Before sending any command, server must compare provided key with stored key. If mismatch, return 403.
- Log all commands in devices/{deviceId}/logs.

TESTING CHECKLIST FOR AI
1. Install app, run wizard, deny one permission, verify Next stays disabled.
2. Reboot phone, verify service starts and appears in Firebase lastSeen within 20 seconds without touching phone.
3. Add device in web UI with wrong key, send ping, verify Android rejects and logs error.
4. Add with correct key, send ping, verify pong.
5. Click Start View, verify system picker appears on phone, after Allow video appears in browser.
6. Send tap command, verify phone screen shows tap.

