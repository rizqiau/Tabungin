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

routes.get("/savings", authenticate, getSavings);
routes.put("/savings", authenticate, updateSaving);
routes.put("/savings/reduce", authenticate, reduceSaving);

routes.post("/goals", authenticate, addGoal);
routes.get("/goals", authenticate, getGoals);
routes.put("/goals", authenticate, updateGoal);
routes.delete("/goals", authenticate, deleteGoal);

export default routes;
