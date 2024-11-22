export class Saving {
    constructor(userId, amount, createdAt, updatedAt ) {
        this.userId = userId;
        this.amount = amount;
        this.createdAt = createdAt || new Date();
        this.updatedAt = updatedAt || new Date();
    }
}
