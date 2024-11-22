export class Goal {
    constructor(userId, title, targetAmount, amount, createdAt, updatedAt) {
        this.userId = userId;
        this.title = title;
        this.targetAmount = targetAmount;
        this.status = this.calculateStatus();
        this.createdAt = createdAt || new Date();
        this.updatedAt = updatedAt || new Date();
    }
    calculateStatus() {
        return this.amount >= this.targetAmount ? "completed" : "on-progress";
    }

    updateAmount(newAmount) {
        this.amount += newAmount;
        this.status = this.calculateStatus();
        this.updatedAt = new Date();
    }
}
