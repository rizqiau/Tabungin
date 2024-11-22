import jwt from "jsonwebtoken";

export const authenticate = (req, res, next) => {
    const token = req.headers.authorization?.split(" ")[1];
    if (!token) {
        return res.status(401).send({ error: "Unauthorized: Token is required." });
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        req.userId = decoded.userId; // Menyimpan userId di request
        next();
    } catch (error) {
        console.error("Invalid token: ", error);
        res.status(403).send({ error: "Invalid token." });
    }
};
