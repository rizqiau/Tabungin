import { db } from "../config/firebase.js";
import { doc, addDoc, collection, getDocs, getDoc, updateDoc, deleteDoc, query, where } from "firebase/firestore";
import { Saving } from "../models/saving.js";
import { User } from "../models/user.js";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";

const usersCollection = collection(db, 'users');

export const addUser = async (req, res) => {
    try {
        const { username, email, password } = req.body;

        if (!username || !email || !password) {
            return res.status(400).send({ error: "Username, email, and password are required." });
        }

        const emailQuery = query(usersCollection, where("email", "==", email));
        const emailSnapshot = await getDocs(emailQuery);

        if (!emailSnapshot.empty) {
            return res.status(400).send({ error: "Email already exists!" });
        }

        const passwordHash = await bcrypt.hash(password, 10);
        const user = new User(username, email, passwordHash);

        const userRef = await addDoc(usersCollection, JSON.parse(JSON.stringify(user)));

        const saving = new Saving(userRef.id, 0);
        const savingsCollectionRef = collection(userRef, "savings");
        await addDoc(savingsCollectionRef, JSON.parse(JSON.stringify(saving)));

        const token = jwt.sign({ userId: userRef.id }, process.env.JWT_SECRET_KEY, {
            expiresIn: process.env.JWT_EXPIRES_IN,
        });

        res.status(201).send({
            message: "User registered successfully!",
            data: {
                user,
                saving: {
                    userId: userRef.id,
                    amount: saving.amount,
                    createdAt: saving.createdAt,
                },
                token,
            },
        });
    } catch (error) {
        console.error("Error adding user: ", error);
        res.status(500).send({ error: "Error adding user!" });
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

export const getSavings = async (req, res) => {
    try {
        const { userId } = req.params;

        const userRef = doc(usersCollection, userId);
        const savingsCollectionRef = collection(userRef, "savings");
        const savingsSnapshot = await getDocs(savingsCollectionRef);

        if (savingsSnapshot.empty) {
            return res.status(404).send({ error: "No savings found!" });
        }

        const savings = savingsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));

        res.json(savings);
    } catch (e) {
        console.error("Error getting savings: ", e);
        res.status(500).send({ error: "Error fetching savings!" });
    }
};

export const updateSaving = async (req, res) => {
    try {
        const { userId } = req.params;
        const { amount } = req.body;

        if (!userId || typeof amount !== "number") {
            return res.status(400).send({ error: 'Amount are required, and amount must be a number.' });
        }

        const userRef = doc(db, "users", userId);
        const savingsCollectionRef = collection(userRef, "savings");

        const savingsSnapshot = await getDocs(savingsCollectionRef);

        if (savingsSnapshot.empty) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingDoc = savingsSnapshot.docs[0]; 
        const savingRef = doc(savingsCollectionRef, savingDoc.id);

        const existingData = savingDoc.data();

        const updatedAmount = existingData.amount + amount;
        const updatedData = {
            amount: updatedAmount,
            updatedAt: new Date(),
        };

        await updateDoc(savingRef, updatedData);

        const goalsCollectionRef = collection(savingRef, "goals");
        const goalsSnapshot = await getDocs(goalsCollectionRef);

        const batchPromises = goalsSnapshot.docs.map(async (goalDoc) => {
            const goalData = goalDoc.data();
            const newStatus = updatedAmount >= goalData.targetAmount ? "Completed" : "On-Progress";

            if (goalData.status !== newStatus) {
                const goalRef = doc(goalsCollectionRef, goalDoc.id);
                await updateDoc(goalRef, { status: newStatus });
            }
        });

        await Promise.all(batchPromises);

        res.status(200).send({
            message: 'Saving updated successfully!',
            data: {
                id: savingDoc.id,
                userId,
                ...updatedData,
            },
        });
    } catch (error) {
        console.error("Error updating saving: ", error);
        res.status(500).send({ error: 'Error updating saving!' });
    }
};


export const reduceSaving = async (req, res) => {
    try {
        const { userId } = req.params;
        const { amount } = req.body;

        if (!userId || typeof amount !== "number" || amount <= 0) {
            return res.status(400).send({ error: 'Positive amount are required.' });
        }

        const userRef = doc(db, "users", userId);
        const savingsCollectionRef = collection(userRef, "savings");

        const savingsSnapshot = await getDocs(savingsCollectionRef);

        if (savingsSnapshot.empty) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingDoc = savingsSnapshot.docs[0];
        const savingRef = doc(savingsCollectionRef, savingDoc.id);

        const existingData = savingDoc.data();

        if (existingData.amount < amount) {
            return res.status(400).send({ error: 'Insufficient saving amount to reduce.' });
        }

        const updatedAmount = existingData.amount - amount;
        const updatedData = {
            amount: updatedAmount,
            updatedAt: new Date(),
        };

        await updateDoc(savingRef, updatedData);

        const goalsCollectionRef = collection(savingRef, "goals");
        const goalsSnapshot = await getDocs(goalsCollectionRef);

        const batchPromises = goalsSnapshot.docs.map(async (goalDoc) => {
            const goalData = goalDoc.data();
            const newStatus = updatedAmount >= goalData.targetAmount ? "Completed" : "On-Progress";

            if (goalData.status !== newStatus) {
                const goalRef = doc(goalsCollectionRef, goalDoc.id);
                await updateDoc(goalRef, { status: newStatus });
            }
        });

        await Promise.all(batchPromises);

        res.status(200).send({
            message: 'Saving reduced successfully!',
            data: {
                id: savingDoc.id,
                userId,
                ...updatedData,
            },
        });
    } catch (error) {
        console.error("Error reducing saving: ", error);
        res.status(500).send({ error: 'Error reducing saving!' });
    }
};



export const addGoal = async (req, res) => {
    try {
        const { userId, savingId } = req.params;
        const { title, targetAmount } = req.body;

        if (!title || !targetAmount) {
            return res.status(400).send({ error: 'title or targetAmount are required.' });
        }

        const savingRef = doc(usersCollection, userId, "savings", savingId);
        const savingSnapshot = await getDoc(savingRef);

        if (!savingSnapshot.exists()) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingData = savingSnapshot.data();
        const totalAmount = savingData.amount;

        const status = totalAmount >= targetAmount ? "Completed" : "On-Progress";

        const goalsCollectionRef = collection(savingRef, "goals");
        const goalData = { title, targetAmount, status };
        const goalRef = await addDoc(goalsCollectionRef, goalData);

        res.status(201).send({
            id: goalRef.id,
            ...goalData,
        });
    } catch (error) {
        console.error("Error adding goal: ", error);
        res.status(500).send({ error: 'Error adding goal!' });
    }
};


export const getGoals = async (req, res) => {
    try {
        const { userId, savingId } = req.params;

        if (!userId || !savingId) {
            return res.status(400).send({ error: 'userId and savingId are required.' });
        }

        const savingRef = doc(db, "users", userId, "savings", savingId);
        const savingSnapshot = await getDoc(savingRef);

        if (!savingSnapshot.exists()) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingData = savingSnapshot.data();

        const goalsCollectionRef = collection(savingRef, "goals");
        const goalsSnapshot = await getDocs(goalsCollectionRef);

        if (goalsSnapshot.empty) {
            return res.status(404).send({ error: 'No goals found.' });
        }

        const goals = goalsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));

        const response = {
            id: savingId,
            userId,
            amount: savingData.amount,
            goals: goals,
        };

        res.status(200).send(response);
    } catch (error) {
        console.error("Error fetching saving with goals: ", error);
        res.status(500).send({ error: 'Error fetching saving with goals!' });
    }
};


export const updateGoal = async (req, res) => {
    try {
        const { userId, savingId, goalId } = req.params;
        const { title, targetAmount } = req.body;

        if (!userId || !savingId || !goalId) {
            return res.status(400).send({ error: 'userId, savingId, and goalId are required.' });
        }

        const goalRef = doc(db, "users", userId, "savings", savingId, "goals", goalId);

        const goalSnapshot = await getDoc(goalRef);
        if (!goalSnapshot.exists()) {
            return res.status(404).send({ error: 'Goal not found.' });
        }

        const goalData = goalSnapshot.data();

        const savingRef = doc(db, "users", userId, "savings", savingId);
        const savingSnapshot = await getDoc(savingRef);

        if (!savingSnapshot.exists()) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingData = savingSnapshot.data();
        const { amount } = savingData;

        const updatedData = {
            title: title || goalData.title,
            targetAmount: targetAmount || goalData.targetAmount,
        };

        const updatedTargetAmount = updatedData.targetAmount;
        updatedData.status = amount >= updatedTargetAmount ? "Completed" : "On-Progress";

        await updateDoc(goalRef, updatedData);

        res.status(200).send({
            id: goalId,
            savingId: savingId,
            ...updatedData,
            amount: amount,
        });
    } catch (error) {
        console.error("Error updating goal: ", error);
        res.status(500).send({ error: 'Error updating goal!' });
    }
};


export const deleteGoal = async (req, res) => {
    try {
        const { userId, savingId, goalId } = req.params;

        if (!userId || !savingId || !goalId) {
            return res.status(400).send({ error: 'userId, savingId, and goalId are required.' });
        }

        const goalRef = doc(db, "users", userId, "savings", savingId, "goals", goalId);

        const goalSnapshot = await getDoc(goalRef);
        if (!goalSnapshot.exists()) {
            return res.status(404).send({ error: 'Goal not found.' });
        }

        await deleteDoc(goalRef);

        res.status(200).send({ message: 'Goal deleted successfully.', goalId, savingId });
    } catch (error) {
        console.error("Error deleting goal: ", error);
        res.status(500).send({ error: 'Error deleting goal!' });
    }
};
