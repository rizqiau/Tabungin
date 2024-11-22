import { getFirestore } from "firebase/firestore";
import { initializeApp } from "firebase/app";

const firebaseConfig = {
  apiKey: "AIzaSyBu6f9G0OiduxpKDDzZuKKAdBTbQ8sXlIc",
  authDomain: "tabungin-app-242208.firebaseapp.com",
  projectId: "tabungin-app-242208",
  storageBucket: "tabungin-app-242208.firebasestorage.app",
  messagingSenderId: "66486896293",
  appId: "1:66486896293:web:70a4d518414054af522f4c",
  measurementId: "G-EFVLQDTP77"
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);