package Server.Common;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

class TransactionHandler implements Serializable {
    private final RMHashMap addedItems = new RMHashMap();
    private final Set<String> deletedItems = new HashSet<>();
    private final RMHashMap resourceData;
    private final int transactionId;

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

        persist();
    }

    synchronized void deleteItem(String key) {
        boolean modified = deletedItems.add(key);
        RMItem valueRemoved = addedItems.remove(key);

        if (modified || valueRemoved != null) {
            persist();
        }
    }

    synchronized void commit() {
        synchronized (resourceData) {
            for (String deletedItem : deletedItems) {
                resourceData.remove(deletedItem);
            }

            resourceData.putAll(addedItems);
        }
    }


    private void persist() {
        try (
                OutputStream file = new FileOutputStream(DATA_PATH + "transaction_" + transactionId);
                ObjectOutputStream outputStream = new ObjectOutputStream(file)
        ) {
            outputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Handle this error (or at least report it)
        }
    }
}
