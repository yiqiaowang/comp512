package Server.Transaction;

public class TransactionAbortedException extends Exception {
    private final int transactionId;

    public TransactionAbortedException(int transactionId) {

        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }
}
