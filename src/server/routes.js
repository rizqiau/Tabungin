// import express from "express";
// import {addSaving, getSavings, updateSaving, deleteSaving} from "./handler.js"

// const routes = express.Router()

// routes.post('/savings', addSaving)

// routes.get('/savings', getSavings)

// routes.put('/savings/:id', updateSaving)

// routes.delete('/savings/:id', deleteSaving)

// export default routes;


import express from "express";
import { addSaving, getSavings, updateSaving, deleteSaving } from "./handler.js";

const routes = express.Router();

// POST - Add new saving
routes.post('/savings', addSaving);

// GET - Get all savings
routes.get('/savings', getSavings);

// PUT - Update saving by ID
routes.put('/savings/:id', updateSaving);

// DELETE - Delete saving by ID
routes.delete('/savings/:id', deleteSaving);

export default routes;
