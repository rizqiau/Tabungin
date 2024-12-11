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

export const getUser = async (req, res) => {
    try {
        const { userId } = req.params;

        const userRef = doc(usersCollection, userId);
        const userSnapshot = await getDoc(userRef);

        if (!userSnapshot.exists()) {
            return res.status(404).send({ error: "User not found." });
        }

        const userData = userSnapshot.data();

        res.status(200).send({
            id: userSnapshot.id,
            ...userData,
        });
    } catch (error) {
        console.error("Error fetching user: ", error);
        res.status(500).send({ error: 'Error fetching user!' });
    }
};

export const deleteUser = async (req, res) => {
    try {
        const { userId } = req.params;

        const userRef = doc(db, "users", userId);
        const userSnapshot = await getDoc(userRef);

        if (!userSnapshot.exists()) {
            return res.status(404).send({ error: "User not found." });
        }

        const savingsCollectionRef = collection(userRef, "savings");
        const savingsSnapshot = await getDocs(savingsCollectionRef);

        if (!savingsSnapshot.empty) {
            for (const savingDoc of savingsSnapshot.docs) {
                const savingRef = doc(savingsCollectionRef, savingDoc.id);
                const additionCollectionRef = collection(savingRef, "addition");
                const reductionCollectionRef = collection(savingRef, "reduction");
                const goalsCollectionRef = collection(savingRef, "goals");

                const additionsSnapshot = await getDocs(additionCollectionRef);
                additionsSnapshot.forEach(async (addDoc) => {
                    await deleteDoc(doc(additionCollectionRef, addDoc.id));
                });

                const reductionsSnapshot = await getDocs(reductionCollectionRef);
                reductionsSnapshot.forEach(async (redDoc) => {
                    await deleteDoc(doc(reductionCollectionRef, redDoc.id));
                });

                const goalsSnapshot = await getDocs(goalsCollectionRef);
                goalsSnapshot.forEach(async (goalDoc) => {
                    await deleteDoc(doc(goalsCollectionRef, goalDoc.id));
                });

                await deleteDoc(savingRef);
            }
        }

        await deleteDoc(userRef);

        res.status(200).send({ message: "User deleted successfully." });
    } catch (error) {
        console.error("Error deleting user: ", error);
        res.status(500).send({ error: 'Error deleting user!' });
    }
}

export const getSavings = async (req, res) => {
    try {
        const { userId } = req.params;

        const userRef = doc(usersCollection, userId);
        const savingsCollectionRef = collection(userRef, "savings");
        const savingsSnapshot = await getDocs(savingsCollectionRef);

        if (savingsSnapshot.empty) {
            return res.status(404).send({ error: "No savings found!" });
        }

        const savingDoc = savingsSnapshot.docs[0];
        const savingRef = doc(savingsCollectionRef, savingDoc.id);

        const additionCollectionRef = collection(savingRef, "addition");
        const additionsSnapshot = await getDocs(additionCollectionRef);
        const additions = additionsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));

        const reductionCollectionRef = collection(savingRef, "reduction");
        const reductionsSnapshot = await getDocs(reductionCollectionRef);
        const reductions = reductionsSnapshot.docs.map((doc) => ({
            id: doc.id,
            ...doc.data(),
        }));

        const savingData = savingDoc.data();

        res.json({
            id: savingDoc.id,
            userId,
            amount: savingData.amount,
            additions: additions,
            reductions: reductions,
        });
    } catch (e) {
        console.error("Error getting savings: ", e);
        res.status(500).send({ error: "Error fetching savings!" });
    }
};

export const deleteTransaction = async (req, res) => {
    try {
        const { transactionId, userId, savingId } = req.params;

        const userRef = doc(db, "users", userId);
        const savingsCollectionRef = collection(userRef, "savings");
        const savingRef = doc(savingsCollectionRef, savingId);

        const savingSnapshot = await getDoc(savingRef);
        if (!savingSnapshot.exists()) {
            return res.status(404).send({ error: 'Saving not found.' });
        }

        let transactionRef = null;
        let transactionCollectionRef = null;

        transactionCollectionRef = collection(savingRef, "addition");
        transactionRef = doc(transactionCollectionRef, transactionId);
        const transactionSnapshot = await getDoc(transactionRef);

        if (transactionSnapshot.exists()) {
            await deleteDoc(transactionRef);
            return res.status(200).send({ message: "Transaction deleted successfully from 'addition'." });
        }

        transactionCollectionRef = collection(savingRef, "reduction");
        transactionRef = doc(transactionCollectionRef, transactionId);

        const transactionSnapshotReduction = await getDoc(transactionRef);

        if (transactionSnapshotReduction.exists()) {
            await deleteDoc(transactionRef);
            return res.status(200).send({ message: "Transaction deleted successfully from 'reduction'." });
        }

        return res.status(404).send({ error: 'Transaction not found in "addition" or "reduction" collections.' });

    } catch (error) {
        console.error("Error deleting transaction: ", error);
        res.status(500).send({ error: "Error deleting transaction!" });
    }
};

export const updateSaving = async (req, res) => {
    try {
        const { userId } = req.params;
        const { amount, description, actionType } = req.body;

        if (!userId || typeof amount !== "number" || amount <= 0 || !description || !actionType) {
            return res.status(400).send({ error: 'Amount, description, and actionType are required.' });
        }

        if (!['add', 'reduce'].includes(actionType)) {
            return res.status(400).send({ error: 'Invalid actionType, must be "add" or "reduce".' });
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
        let updatedAmount;

        if (actionType === 'add') {
            updatedAmount = existingData.amount + amount;
        } else if (actionType === 'reduce') {
            if (existingData.amount < amount) {
                return res.status(400).send({ error: 'Insufficient saving amount to reduce.' });
            }
            updatedAmount = existingData.amount - amount;
        }

        const updatedData = {
            amount: updatedAmount,
            updatedAt: new Date(),
        };

        await updateDoc(savingRef, updatedData);

        const transactionCollectionRef = actionType === 'add' ? collection(savingRef, "addition") : collection(savingRef, "reduction");
        const transactionData = {
            amount,
            description,
            createdAt: new Date(),
        };

        await addDoc(transactionCollectionRef, transactionData);

        const goalsCollectionRef = collection(savingRef, "goals");
        const goalsSnapshot = await getDocs(goalsCollectionRef);

        goalsSnapshot.docs.forEach(async (goalDoc) => {
            const goalRef = doc(goalsCollectionRef, goalDoc.id);
            const goalData = goalDoc.data();

            const updatedStatus = updatedAmount >= goalData.targetAmount ? "Completed" : "On-Progress";
            await updateDoc(goalRef, { status: updatedStatus });
        });

        res.status(200).send({
            message: `${actionType.charAt(0).toUpperCase() + actionType.slice(1)} saving successfully!`,
            data: {
                id: savingDoc.id,
                userId,
                ...updatedData,
                transaction: transactionData,
            },
        });
    } catch (error) {
        console.error(`Error updating saving: `, error);
        res.status(500).send({ error: 'Error updating saving!' });
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
