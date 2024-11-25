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

export const loginUser = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).send({ error: "Email dan password harus diisi." });
        }

        const emailQuery = query(usersCollection, where("email", "==", email));
        const emailSnapshot = await getDocs(emailQuery);

        if (emailSnapshot.empty) {
            return res.status(404).send({ error: "Email tidak ditemukan." });
        }

        const userDoc = emailSnapshot.docs[0];
        const user = userDoc.data();

        const passwordMatch = await bcrypt.compare(password, user.passwordHash);

        if (!passwordMatch) {
            return res.status(401).send({ error: "Password salah." });
        }

        const token = jwt.sign({ userId: userDoc.id }, process.env.JWT_SECRET_KEY, {
            expiresIn: process.env.JWT_EXPIRES_IN,
        });

        res.status(200).send({
            message: "Login berhasil.",
            token,
        });
    } catch (error) {
        console.error("Error saat login: ", error);
        res.status(500).send({ error: "Error saat login!" });
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
        const { userId } = req.body;

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
        const { userId, amount } = req.body;

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
        const { userId, amount } = req.body;

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
        const { savingId, title, targetAmount } = req.body;

        if (!title || !targetAmount || !savingId) {
            return res.status(400).send({ error: 'Saving ID, title, and target amount are required.' });
        }

        const usersSnapshot = await getDocs(collection(db, "users"));
        let savingRef = null;

        for (const userDoc of usersSnapshot.docs) {
            const savingsRef = collection(userDoc.ref, "savings");
            const savingQuery = query(savingsRef, where("__name__", "==", savingId));
            const savingSnapshot = await getDocs(savingQuery);

            if (!savingSnapshot.empty) {
                savingRef = savingSnapshot.docs[0].ref;
                break;
            }
        }

        if (!savingRef) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingData = (await getDoc(savingRef)).data();
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
        const { savingId } = req.body;

        if (!savingId) {
            return res.status(400).send({ error: 'Saving ID is required.' });
        }

        const usersSnapshot = await getDocs(collection(db, "users"));
        let savingRef = null;

        for (const userDoc of usersSnapshot.docs) {
            const savingsRef = collection(userDoc.ref, "savings");
            const savingQuery = query(savingsRef, where("__name__", "==", savingId));
            const savingSnapshot = await getDocs(savingQuery);

            if (!savingSnapshot.empty) {
                savingRef = savingSnapshot.docs[0].ref;
                break;
            }
        }

        if (!savingRef) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        const savingData = (await getDoc(savingRef)).data();
        const goalsCollectionRef = collection(savingRef, "goals");
        const goalsSnapshot = await getDocs(goalsCollectionRef);

        if (goalsSnapshot.empty) {
            return res.status(404).send({ error: 'No goals found.' });
        }

        const goals = goalsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));

        res.status(200).send({
            savingId,
            amount: savingData.amount,
            goals,
        });
    } catch (error) {
        console.error("Error fetching goals: ", error);
        res.status(500).send({ error: 'Error fetching goals!' });
    }
};



export const updateGoal = async (req, res) => {
    try {
        const { goalId, title, targetAmount } = req.body;

        if (!goalId) {
            return res.status(400).send({ error: 'goalId is required.' });
        }

        const usersSnapshot = await getDocs(collection(db, "users"));
        let goalRef = null;
        let savingRef = null;

        for (const userDoc of usersSnapshot.docs) {
            const savingsCollectionRef = collection(userDoc.ref, "savings");
            const savingsSnapshot = await getDocs(savingsCollectionRef);

            for (const savingDoc of savingsSnapshot.docs) {
                const goalsCollectionRef = collection(savingDoc.ref, "goals");
                const goalQuery = query(goalsCollectionRef, where("__name__", "==", goalId));
                const goalSnapshot = await getDocs(goalQuery);

                if (!goalSnapshot.empty) {
                    goalRef = goalSnapshot.docs[0].ref;
                    savingRef = savingDoc.ref;
                    break;
                }
            }

            if (goalRef) break;
        }

        if (!goalRef || !savingRef) {
            return res.status(404).send({ error: 'Goal not found.' });
        }

        const savingData = (await getDoc(savingRef)).data();
        const amount = savingData.amount;

        const updatedData = {
            title: title || (await getDoc(goalRef)).data().title,
            targetAmount: targetAmount || (await getDoc(goalRef)).data().targetAmount,
        };

        updatedData.status = amount >= updatedData.targetAmount ? "Completed" : "On-Progress";

        await updateDoc(goalRef, updatedData);

        res.status(200).send({
            goalId,
            ...updatedData,
            amount,
        });
    } catch (error) {
        console.error("Error updating goal: ", error);
        res.status(500).send({ error: 'Error updating goal!' });
    }
};



export const deleteGoal = async (req, res) => {
    try {
        const { goalId } = req.body;

        if (!goalId) {
            return res.status(400).send({ error: 'goalId is required.' });
        }

        const usersSnapshot = await getDocs(collection(db, "users"));
        let goalRef = null;

        for (const userDoc of usersSnapshot.docs) {
            const savingsCollectionRef = collection(userDoc.ref, "savings");
            const savingsSnapshot = await getDocs(savingsCollectionRef);

            for (const savingDoc of savingsSnapshot.docs) {
                const goalsCollectionRef = collection(savingDoc.ref, "goals");
                const goalQuery = query(goalsCollectionRef, where("__name__", "==", goalId));
                const goalSnapshot = await getDocs(goalQuery);

                if (!goalSnapshot.empty) {
                    goalRef = goalSnapshot.docs[0].ref;
                    break;
                }
            }

            if (goalRef) break;
        }

        if (!goalRef) {
            return res.status(404).send({ error: 'Goal not found.' });
        }

        await deleteDoc(goalRef);

        res.status(200).send({ message: 'Goal deleted successfully.', goalId });
    } catch (error) {
        console.error("Error deleting goal: ", error);
        res.status(500).send({ error: 'Error deleting goal!' });
    }
};

