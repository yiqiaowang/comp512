package Server.Transaction;

public class InvalidTransactionException extends Exception {
    private final int transactionId;

    public InvalidTransactionException(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }
}
