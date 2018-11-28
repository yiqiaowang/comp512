package Server.Common;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import static Server.Common.TransactionDecision.*;

class TransactionHandler implements Serializable {
    private final RMHashMap addedItems = new RMHashMap();
    private final Set<String> deletedItems = new HashSet<>();
    private final RMHashMap resourceData;
    private final int transactionId;

    private volatile boolean receivedVoteRequest = false;
    private volatile boolean sentResponse = false;
    private volatile TransactionDecision vote = IN_PROGRESS;
    private volatile TransactionDecision finalDecision = IN_PROGRESS;


    public static final String DATA_PATH = "./transaction_data/";

    TransactionHandler(RMHashMap resourceData, int transactionId) {
        this.resourceData = resourceData;
        this.transactionId = transactionId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    synchronized RMItem readItem(String key) {
        if (deletedItems.contains(key)) {
            return null;
        }

        RMItem item = addedItems.get(key);
        if (item == null) {
            item = resourceData.get(key);
        }

        if (item != null) {
            return (RMItem) item.clone();
        } else {
            return null;
        }
    }

    synchronized void addItem(String key, RMItem value) {
        addedItems.put(key, value);
        deletedItems.remove(key);
    }

    synchronized void deleteItem(String key) {
        deletedItems.add(key);
        addedItems.remove(key);
    }

    synchronized boolean commit() {
        // TODO: change state of committed or not
        synchronized (resourceData) {
            for (String deletedItem : deletedItems) {
                resourceData.remove(deletedItem);
            }

            resourceData.putAll(addedItems);
        }

        System.out.println("Resource data is now " + resourceData);


        return true;
    }



    /**
     * This method is called when a resource manager is asked to vote on a transaction.
     * @return true for commit, false for abort.
     */
    public synchronized boolean vote() {
        boolean requestStatus = receivedVoteRequest;
        receivedVoteRequest = true;

        if (!requestStatus && finalDecision == IN_PROGRESS) {
            vote = COMMIT;
        } else {
            vote = ABORT;
        }

        sentResponse = true;
        return vote == COMMIT;
    }

    @Override
    public String toString() {
        return "TransactionHandler{\n" +
                "addedItems=" + addedItems +
                "\n, deletedItems=" + deletedItems +
                "\n, resourceData=" + resourceData +
                "\n, transactionId=" + transactionId +
                "\n, receivedVoteRequest=" + receivedVoteRequest +
                "\n, sentResponse=" + sentResponse +
                "\n, vote=" + vote +
                "\n, finalDecision=" + finalDecision +
                "\n}";
    }
}
