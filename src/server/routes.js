import express from "express";
import {addSaving, getSavings, updateSaving, deleteSaving} from "./handler.js"

const routes = express.Router()

routes.post('/savings', addSaving)

routes.get('/savings', getSavings)

routes.put('/savings/:id', updateSaving)

routes.delete('/savings/:id', deleteSaving)

export default routes;