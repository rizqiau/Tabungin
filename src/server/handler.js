import { db } from "../config/firebase.js";
import { doc, addDoc, collection, getDocs, updateDoc, query, where } from "firebase/firestore";
import { Saving } from "../models/saving.js";
import { User } from "../models/user.js";
import { Goal } from "../models/goal.js";
import bcrypt from "bcrypt";

const usersCollection = collection(db, 'users');
const savingsCollection = collection(db, 'savings');
const goalsCollection = collection(db, 'goals');

export const addUser = async (req, res) => {
    try {
        const { username, email, password } = req.body;

        if (!username || !email || !password) {
            return res.status(400).send({ error: 'Username, email, and password are required.' });
        }

        const emailQuery = query(usersCollection, where("email", "==", email));
        const emailSnapshot = await getDocs(emailQuery);

        if (!emailSnapshot.empty) {
            return res.status(400).send({ error: 'Email already exists!' });
        }

        const passwordHash = await bcrypt.hash(password, 10);

        const user = new User(username, email, passwordHash);
        await addDoc(usersCollection, JSON.parse(JSON.stringify(user)));

        res.status(201).send({ message: 'User registered successfully!', data: user });
    } catch (error) {
        console.error("Error adding user: ", error);
        res.status(500).send({ error: 'Error adding user!' });
    }
};

export const getUsers = async (req, res) => {
    try {
        const usersSnapshot = await getDocs(usersCollection);
        const users = usersSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));
        res.json(users);
    } catch (error) {
        console.error("Error fetching users: ", error);
        res.status(500).send({ error: 'Error fetching users!' });
    }
};

export const addSaving = async (req, res) => {
    try {
        const { userId } = req.body;

        const userIdQuery = query(savingsCollection, where("userId", "==", userId));
        const userIdSnapshot = await getDocs(userIdQuery);

        if (!userIdSnapshot.empty) {
            return res.status(400).send({ error: 'userId already exists! Please use updateSaving endpoint.' });
        }

        const saving = new Saving(
            req.body.userId,
            req.body.amount
        );
        await addDoc(savingsCollection, JSON.parse(JSON.stringify(saving)));
        res.status(201).send({ message: 'Saving added successfully!', data: saving });
    } catch (e) {
        console.error("Error adding saving: ", e);
        res.status(500).send({ error: 'Error adding saving!' });
    }
};

export const getSavings = async (req, res) => {
    try {
        const savingsSnapshot = await getDocs(savingsCollection);
        const savings = savingsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));
        res.json(savings);
    } catch (e) {
        console.error("Error getting savings: ", e);
        res.status(500).send({ error: 'Error fetching savings!' });
    }
};

export const updateSaving = async (req, res) => {
    try {
        const { userId, amount } = req.body;

        if (!userId || typeof amount !== "number") {
            return res.status(400).send({ error: 'UserId and amount are required, and amount must be a number.' });
        }

        const savingQuery = query(savingsCollection, where("userId", "==", userId));
        const savingsSnapshot = await getDocs(savingQuery);

        if (savingsSnapshot.empty) {
            return res.status(404).send({ error: 'Saving not found for the given userId.' });
        }

        const savingDoc = savingsSnapshot.docs[0];
        const savingRef = doc(db, "savings", savingDoc.id);

        const existingData = savingDoc.data();

        const updatedData = {
            amount: existingData.amount + amount, 
            updatedAt: new Date(),
        };

        await updateDoc(savingRef, updatedData);

        res.status(200).send({
            message: 'Saving updated successfully!',
            data: {
                id: savingDoc.id,
                userId: existingData.userId,
                ...updatedData,
            },
        });
    } catch (e) {
        console.error("Error updating saving: ", e);
        res.status(500).send({ error: 'Error updating saving!' });
    }
};

export const addGoal = async (req, res) => {
    try {
        const { userId, title, targetAmount } = req.body;

        if (!userId || !title || !targetAmount) {
            return res.status(400).send({ error: 'UserId, title, and targetAmount are required.' });
        }

        const savingsQuery = query(savingsCollection, where("userId", "==", userId));
        const savingsSnapshot = await getDocs(savingsQuery);

        const totalAmount = savingsSnapshot.docs.reduce((sum, doc) => sum + doc.data().amount, 0);

        const status = totalAmount >= targetAmount ? "completed" : "on-progress";

        const goal = new Goal(userId, title, targetAmount, totalAmount);
        const docRef = await addDoc(goalsCollection, JSON.parse(JSON.stringify(goal)));

        res.status(201).send({
            id: docRef.id,
            userId: goal.userId,
            title: goal.title,
            targetAmount: goal.targetAmount,
            amount: goal.amount,
            status: goal.status,
        });
    } catch (error) {
        console.error("Error adding goal: ", error);
        res.status(500).send({ error: 'Error adding goal!' });
    }
};

export const getGoals = async (req, res) => {
    try {
        const goalsSnapshot = await getDocs(goalsCollection);
        const goals = goalsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));
        res.json(goals);
    } catch (error) {
        console.error("Error fetching goals: ", error);
        res.status(500).send({ error: 'Error fetching goals!' });
    }
};