import { db } from "../config/firebase.js";
import { addDoc, getDoc, updateDoc, deleteDoc, collection, getDocs } from "firebase/firestore";
import { Saving } from "../models/saving.js";

const savingsCollection = collection(db, 'savings');

export const addSaving = async (req, res) => {
    try {
        const saving = new Saving(
            req.body.name,
            req.body.amount,
            req.body.goal,
            req.body.status,
        )
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

// Function to update saving in Firestore
export const updateSaving = async (req, res) => {
    const { id } = req.params;
    const { name, amount, goal } = req.body;

    try {
        const savingRef = doc(db, "savings", id);

        // Fetch the saving document
        const savingDoc = await getDoc(savingRef);
        if (!savingDoc.exists()) {
            return res.status(404).send({ error: 'Saving not found!' });
        }

        const updatedData = {
            name: name || savingDoc.data().name,
            amount: amount ? savingDoc.data().amount + parseFloat(amount) : savingDoc.data().amount,
            goal: goal || savingDoc.data().goal,
            status: amount >= goal ? 'completed' : 'in-progress',
        };

        // Update the saving document
        await updateDoc(savingRef, updatedData);

        res.status(200).send({ message: `Saving updated successfully for ID: ${id}`, data: updatedData });
    } catch (e) {
        console.error("Error updating saving: ", e);
        res.status(500).send({ error: 'Error updating saving!' });
    }
};

// Function to delete saving in Firestore
export const deleteSaving = async (req, res) => {
    const { id } = req.params;

    try {
        const savingRef = doc(db, "savings", id);

        // Fetch the saving document
        const savingDoc = await getDoc(savingRef);
        if (!savingDoc.exists()) {
            return res.status(404).send({ error: 'Saving not found!' });
        }

        // Delete the saving document
        await deleteDoc(savingRef);

        res.status(200).send({ message: `Saving deleted successfully for ID: ${id}` });
    } catch (e) {
        console.error("Error deleting saving: ", e);
        res.status(500).send({ error: 'Error deleting saving!' });
    }
};
