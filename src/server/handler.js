let savingsData = [];

export const addSaving = (req, res) => {
    const { name, amount, goal } = req.body;

    if (!name || !amount || !goal) {
        return res.status(400).send({ error: 'Name, amount, and goal are required!' });
    }

    const newSaving = {
        id: savingsData.length + 1,
        name,
        amount: parseFloat(amount),
        goal: parseFloat(goal),
        status: amount >= goal ? 'completed' : 'in-progress',
        createdAt: new Date(),
    }
    savingsData.push(newSaving);
    res.status(201).send({ message: 'Saving added successfully!', data: newSaving });
}

export const getSavings = (req, res) => {
    res.send({ data: savingsData });
}

export const updateSaving = (req, res) => {
    const { id } = req.params;
    const { name, amount, goal } = req.body;

    const saving = savingsData.find(saving => saving.id === parseInt(id));
    console.log(saving);

    if (!saving) {
        return res.status(404).send({ error: 'Saving not found!' });
    }

    if (name) saving.name = name;
    if (amount) saving.amount += parseFloat(amount);;
    if (goal) saving.goal = parseFloat(goal);

    if (saving.amount >= saving.goal) {
        saving.status = 'completed';
    } else {
        saving.status = 'in-progress';
    }
    return res.status(200).send({ message: `Saving amount updated successfully! for ID: ${id}`, data: saving });
}

export const deleteSaving = (req, res) => {
    const { id } = req.params;
    const savingIndex = savingsData.findIndex(saving => saving.id === parseInt(id));
    if (savingIndex === -1) {
        return res.status(404).send({ error: 'Saving not found!' });
    }
    savingsData.splice(savingIndex, 1);
    return res.status(200).send({ message: `Saving deleted successfully! for ID: ${id}` });
}