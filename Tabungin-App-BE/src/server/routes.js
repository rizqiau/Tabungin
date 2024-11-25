import {
    getSavings,
    updateSaving,
    reduceSaving,
    addUser,
    loginUser,
    getUsers,
    addGoal,
    getGoals,
    updateGoal,
    deleteGoal,
} from "./handler.js";
import express from "express";
import { authenticate } from "../middleware/auth.js";

const routes = express.Router();

routes.post("/users", addUser);
routes.post("/login", loginUser);
routes.get("/users", authenticate, getUsers);

routes.get("/savings/:userId", authenticate, getSavings);
routes.put("/savings/:userId", authenticate, updateSaving);
routes.put("/savings/reduce/:userId", authenticate, reduceSaving);

routes.post("/goals/:userId/:savingId", authenticate, addGoal);
routes.get("/goals/:userId/:savingId", authenticate, getGoals);
routes.put("/goals/:userId/:savingId/:goalId", authenticate, updateGoal);
routes.delete("/goals/:userId/:savingId/:goalId", authenticate, deleteGoal);

export default routes;
