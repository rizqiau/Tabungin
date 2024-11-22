import {
    getSavings,
    updateSaving,
    reduceSaving,
    addUser,
    getUsers,
    addGoal,
    getGoals,
    updateGoal,
    deleteGoal,
} from "./handler.js";
import express from "express";
import { authenticate } from "../middleware/auth.js"; // Middleware autentikasi

const routes = express.Router();

routes.post("/users", addUser); // Tidak memerlukan autentikasi
routes.get("/users", authenticate, getUsers);

routes.get("/savings/:userId", authenticate, getSavings);
routes.put("/savings/:userId", authenticate, updateSaving);
routes.put("/savings/reduce/:userId", authenticate, reduceSaving);

routes.post("/goals/:savingId", authenticate, addGoal);
routes.get("/goals/:savingId", authenticate, getGoals);
routes.put("/goals/:savingId/:goalId", authenticate, updateGoal);
routes.delete("/goals/:savingId/:goalId", authenticate, deleteGoal);

export default routes;
