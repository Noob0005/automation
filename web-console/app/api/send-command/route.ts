import { NextRequest, NextResponse } from 'next/server';
import { getFirestore, doc, getDoc, addDoc, collection, serverTimestamp } from 'firebase-admin/firestore';
import { getMessaging } from 'firebase-admin/messaging';
import { initializeApp, getApps, cert } from 'firebase-admin/app';

// Initialize Firebase Admin (requires service account)
if (!getApps().length) {
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    initializeApp({
      credential: cert(JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT)),
    });
  }
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const { deviceId, commandType, deviceKey, payload } = body;

    if (!deviceId || !commandType || !deviceKey) {
      return NextResponse.json({ error: 'Missing required fields' }, { status: 400 });
    }

    const db = getFirestore();
    const deviceRef = doc(db, 'devices', deviceId);
    const deviceSnap = await getDoc(deviceRef);

    if (!deviceSnap.exists()) {
      return NextResponse.json({ error: 'Device not found' }, { status: 404 });
    }

    const device = deviceSnap.data();

    // Verify device key
    if (device.deviceKey !== deviceKey) {
      return NextResponse.json({ error: 'Invalid device key' }, { status: 401 });
    }

    // Check if device is online (seen in last 5 minutes)
    const lastSeen = device.lastSeen?.toMillis?.() || 0;
    const isOnline = Date.now() - lastSeen < 5 * 60 * 1000;

    if (!isOnline) {
      return NextResponse.json({ 
        error: 'Device appears offline', 
        warning: 'Command will be queued but may not execute immediately' 
      }, { status: 202 });
    }

    // Create command
    const commandId = `cmd_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
    const command = {
      commandId,
      type: commandType,
      deviceKey,
      timestamp: Date.now(),
      payload: payload || null,
    };

    // Send via FCM
    if (device.fcmToken) {
      const messaging = getMessaging();
      await messaging.send({
        token: device.fcmToken,
        data: { command: JSON.stringify(command) },
      });
    }

    // Store command in Firestore
    await addDoc(collection(db, 'commands'), {
      ...command,
      deviceId,
      status: 'sent',
      createdAt: serverTimestamp(),
    });

    return NextResponse.json({ 
      success: true, 
      commandId,
      message: 'Command sent successfully' 
    });

  } catch (error) {
    console.error('Send command error:', error);
    return NextResponse.json({ error: 'Internal server error' }, { status: 500 });
  }
}
