'use client';

import { useState, useEffect } from 'react';
import { signOut, onAuthStateChanged, User } from 'firebase/auth';
import { auth, db } from '../lib/firebase';
import { collection, query, where, getDocs, doc, updateDoc, serverTimestamp } from 'firebase/firestore';
import { useRouter } from 'next/navigation';

interface Device {
  id: string;
  deviceId: string;
  deviceKey: string;
  fcmToken: string;
  model: string;
  lastSeen: any;
  status: string;
}

export default function Dashboard() {
  const [user, setUser] = useState<User | null>(null);
  const [devices, setDevices] = useState<Device[]>([]);
  const [loading, setLoading] = useState(true);
  const [newDeviceKey, setNewDeviceKey] = useState('');
  const router = useRouter();

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      if (!currentUser) {
        router.push('/');
        return;
      }
      setUser(currentUser);
      loadDevices();
    });
    return () => unsubscribe();
  }, [router]);

  const loadDevices = async () => {
    if (!user) return;
    try {
      const q = query(collection(db, 'devices'), where('userId', '==', user.uid));
      const snapshot = await getDocs(q);
      const deviceList: Device[] = [];
      snapshot.forEach((docSnapshot) => {
        deviceList.push({ id: docSnapshot.id, ...docSnapshot.data() } as Device);
      });
      setDevices(deviceList);
    } catch (error) {
      console.error('Error loading devices:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddDevice = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user || !newDeviceKey.trim()) return;
    try {
      const q = query(collection(db, 'devices'), where('deviceKey', '==', newDeviceKey.trim()));
      const snapshot = await getDocs(q);
      if (snapshot.empty) {
        alert('Device not found. Please enter the correct 32-character device key.');
        return;
      }
      const deviceDoc = snapshot.docs[0];
      await updateDoc(doc(db, 'devices', deviceDoc.id), {
        userId: user.uid,
        addedAt: serverTimestamp(),
      });
      setNewDeviceKey('');
      loadDevices();
      alert('Device added successfully!');
    } catch (error) {
      console.error('Error adding device:', error);
      alert('Failed to add device');
    }
  };

  const handleSignOut = async () => {
    await signOut(auth);
    router.push('/');
  };

  if (loading) return <div className="min-h-screen flex items-center justify-center">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold text-gray-800">MyRemote Admin</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-sm text-gray-600">{user?.email}</span>
              <button onClick={handleSignOut} className="text-sm text-red-600 hover:text-red-700">Sign Out</button>
            </div>
          </div>
        </div>
      </nav>
      <main className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h2 className="text-lg font-semibold mb-4">Add New Device</h2>
          <form onSubmit={handleAddDevice} className="flex gap-4">
            <input type="text" value={newDeviceKey} onChange={(e) => setNewDeviceKey(e.target.value)}
              placeholder="Enter 32-character device key" className="flex-1 px-4 py-2 border border-gray-300 rounded-lg" maxLength={32} />
            <button type="submit" className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Add Device</button>
          </form>
        </div>
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold">Your Devices ({devices.length})</h2>
          </div>
          {devices.length === 0 ? (
            <div className="px-6 py-12 text-center text-gray-500">No devices added yet.</div>
          ) : (
            <div className="divide-y divide-gray-200">
              {devices.map((device) => (
                <div key={device.id} className="px-6 py-4 hover:bg-gray-50 flex items-center justify-between">
                  <div>
                    <h3 className="font-medium text-gray-900">{device.model || 'Android Device'}</h3>
                    <p className="text-sm text-gray-500">Key: {device.deviceKey.substring(0, 8)}...</p>
                  </div>
                  <a href={`/dashboard/${device.id}`} className="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 text-sm font-medium">Open Console</a>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
