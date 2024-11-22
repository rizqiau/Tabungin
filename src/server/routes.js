import { getSavings, updateSaving, reduceSaving, addUser, getUsers, addGoal, getGoals, updateGoal, deleteGoal } from "./handler.js";
import express from "express";

const routes = express.Router();

routes.post('/users', addUser);
routes.get('/users', getUsers);

routes.get('/savings', getSavings);
routes.put('/savings/:userId', updateSaving);
routes.put('/savings/reduce/:userId', reduceSaving);

routes.post('/goals/:savingId', addGoal);
routes.get('/goals/:savingId', getGoals);
routes.put('/goals/:savingId/:goalId', updateGoal);
routes.delete('/goals/:savingId/:goalId', deleteGoal);


export default routes;
