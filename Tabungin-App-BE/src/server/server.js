import express from "express";
import routes from "./routes.js";
import bodyParser from "body-parser";

const app = express()
const port = 9000

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

app.use('/', routes)

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})