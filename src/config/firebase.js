import { getFirestore } from "firebase/firestore";
// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
// import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBu6f9G0OiduxpKDDzZuKKAdBTbQ8sXlIc",
  authDomain: "tabungin-app-242208.firebaseapp.com",
  projectId: "tabungin-app-242208",
  storageBucket: "tabungin-app-242208.firebasestorage.app",
  messagingSenderId: "66486896293",
  appId: "1:66486896293:web:70a4d518414054af522f4c",
  measurementId: "G-EFVLQDTP77"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
// const analytics = getAnalytics(app);
export const db = getFirestore(app);