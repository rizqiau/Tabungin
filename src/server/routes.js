import express from "express";
import {addSaving, getSavings} from "./handler.js"

const routes = express.Router()

routes.post('/savings', addSaving)

routes.get('/savings', getSavings)

export default routes;