importScripts("https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js");
importScripts("https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js");

// We keep this minimal because we only need it to receive background push messages.
// The actual config is passed implicitly by Vercel/Firebase, or we can use generic messaging.
firebase.initializeApp({
  apiKey: "AIzaSyCwKJw72RXUzvvBLdxI3WlfCoWvFPmvv3Y",
  authDomain: "student-planner-95ed4.firebaseapp.com",
  projectId: "student-planner-95ed4",
  storageBucket: "student-planner-95ed4.firebasestorage.app",
  messagingSenderId: "224841353755",
  appId: "1:224841353755:web:0defe4eccd9a222a9646be",
  measurementId: "G-GK068Y52DV"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  console.log("[firebase-messaging-sw.js] Received background message ", payload);
  
  const notificationTitle = payload.notification?.title || "Your Day Reminder";
  const notificationOptions = {
    body: payload.notification?.body || "You have a task due soon.",
    icon: "/icons/icon-192x192.png",
    badge: "/icons/icon-192x192.png",
    data: payload.data,
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
