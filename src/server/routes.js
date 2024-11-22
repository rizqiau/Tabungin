import { addSaving, getSavings, updateSaving, addUser, getUsers, addGoal, getGoals, updateGoal } from "./handler.js";
import express from "express";

const routes = express.Router();

routes.post('/users', addUser);
routes.get('/users', getUsers);

routes.post('/savings', addSaving);
routes.get('/savings', getSavings);
routes.put('/savings', updateSaving);

routes.post('/goals', addGoal);
routes.get('/goals/:savingId', getGoals);
routes.put('/goals/:savingId', updateGoal);


export default routes;
