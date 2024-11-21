let savingsData = [];

export const addSaving = (req, res) => {
    const { name, amount } = req.body;

    if (!name || !amount) {
        return res.status(400).send({ error: 'Name and amount are required!' });
    }

    const existingSaving = savingsData.find(saving => saving.name === name);

    if (existingSaving) {
        existingSaving.amount += parseFloat(amount);
        return res.send({ message: `Amount updated for ${name}!`, data: existingSaving });
    } else {
        const newSaving = {
            id: savingsData.length + 1,
            name,
            amount: parseFloat(amount),
            createdAt: new Date(),
        }
        savingsData.push(newSaving);
        res.status(201).send({ message: 'Saving added successfully!', data: newSaving });
    }
}

export const getSavings = (req, res) => {
    res.send({ data: savingsData });
}