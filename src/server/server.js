import express from "express";
import routes from "./routes.js";

const app = express()
const port = 9000

// middleware
app.use(express.json())

app.use('/', routes)

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})