package Server.Transaction;

import Server.Interface.InvalidTransactionException;
import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;
import Server.Common.ChaosMonkey;
import Server.Common.CrashModes;


import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager implements Serializable {

    public ChaosMonkey chaosMonkey;
    private Map<Integer, Transaction> transactions;
    private AtomicInteger transactionIdGenerator;
    private LockManager lockManager;

    private static final String ACTIVE_TRANSACTIONS_LOG = "./middleware_active_transactions";

    private void startCheckForTimeouts() {
        Thread checkForTimeouts = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                List<Integer> timedOutTransactionIds = new ArrayList<>();

                for (Transaction transaction : transactions.values()) {
                    if (transaction.checkForTimeout()) {
                        timedOutTransactionIds.add(transaction.getTransactionId());
                    }
                }

                for (Integer timedOutTransactionId : timedOutTransactionIds) {
                    System.out.println("Transaction " + timedOutTransactionId + " has timed out");
                    abort(timedOutTransactionId);
                }

                if (timedOutTransactionIds.size() > 0) {
                    persistActiveTransactions();
                }
            }
        });
        checkForTimeouts.start();
    }


    public static TransactionManager initialize() {
        System.out.println("Initialize transaction manager called");

        TransactionManager transactionManager;

        try (
                InputStream file = new FileInputStream(ACTIVE_TRANSACTIONS_LOG);
                ObjectInputStream inputStream = new ObjectInputStream(file)
        ) {
            System.out.println("Created and returning transaction manager object from file");
            transactionManager = (TransactionManager) inputStream.readObject();
        }
        // TODO: Log any errors or handle them
        catch (IOException e) {
            // If this happens, then there is no data to read. Initialize with default values
            transactionManager = new TransactionManager();
            transactionManager.lockManager = new LockManager();
            transactionManager.transactionIdGenerator = new AtomicInteger(0);
            transactionManager.transactions = new ConcurrentHashMap<>();
            transactionManager.chaosMonkey = new ChaosMonkey();
        } catch (ClassNotFoundException e) {
            // Should never happen
            System.out.println("Class not found exception happened in Transaction Manager's initialize: ");
            e.printStackTrace();
            return null;
        }

        transactionManager.startCheckForTimeouts();

        return transactionManager;
    }


    public int startTransaction() {
        int transactionId = transactionIdGenerator.incrementAndGet();
        Transaction transaction = new Transaction(transactionId);
        transactions.put(transactionId, transaction);

        persistActiveTransactions();

        return transactionId;
    }

    public boolean requestLocksOnResources(int transactionId, List<ResourceLockRequest> resourceLockRequests) {
        ResourceLockRequest[] requests = new ResourceLockRequest[resourceLockRequests.size()];
        for (int i = 0; i < resourceLockRequests.size(); i++) {
            requests[i] = resourceLockRequests.get(i);
        }
        return requestLocksOnResources(transactionId, requests);
    }

    public boolean requestLocksOnResources(int transactionId, ResourceLockRequest... resourceLockRequests) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return false;
        }

        synchronized (transaction.lock)
        {
            for (ResourceLockRequest resourceLockRequest : resourceLockRequests) {
                try {
                    boolean lockAcquired = lockManager.Lock(transactionId, resourceLockRequest.getResourceName(), resourceLockRequest.getLockType());
                    if (!lockAcquired) {
                        System.out.println("Failed to acquire " + resourceLockRequest.getLockType() + " on " + resourceLockRequest.getResourceName());
                        abort(transactionId);
                        break;
                    }
                } catch (DeadlockException e) {
                    System.out.println("Failed to acquire " + resourceLockRequest.getLockType() + " on " + resourceLockRequest.getResourceName() + " because of deadlock");
                    abort(transactionId);
                    break;
                }

                transaction.addResourceManager(resourceLockRequest.getResourceManager(), resourceLockRequest.getResourceName());
            }

            if (resourceLockRequests.length > 0) {
                persistActiveTransactions();
            }

            return true;
        }
    }

    public boolean commit(int transactionId) throws RemoteException {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return false;
        }

        boolean commitResult = transaction.commit();
        transactions.remove(transactionId);
        lockManager.UnlockAll(transactionId);

        persistActiveTransactions();

        return commitResult;
    }

    public void abort(int transactionId) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return;
        }

        transaction.abort();
        transactions.remove(transactionId);
        lockManager.UnlockAll(transactionId);

        persistActiveTransactions();
    }

    public boolean isOngoingTransaction(int transactionId) {
        return transactions.containsKey(transactionId);
    }

    public boolean startTransaction(int transactionId) {
        if (!isOngoingTransaction(transactionId)) {
            Transaction transaction = new Transaction(transactionId);
            transactions.put(transactionId, transaction);

            persistActiveTransactions();
            return true;
        } else {
            return false;
        }
    }


    public synchronized void persistActiveTransactions() {
        try (
                OutputStream file = new FileOutputStream(ACTIVE_TRANSACTIONS_LOG);
                ObjectOutputStream outputStream = new ObjectOutputStream(file)
        ) {
            outputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean prepare(int transactionId) throws RemoteException, InvalidTransactionException {
        
        // TransactionManager crash mode 1
        this.chaosMonkey.crashIfEnabled(CrashModes.T_ONE);
        
        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new InvalidTransactionException(transactionId);
        }

        // TransactionManager crash mode 2
        if (this.chaosMonkey.checkIfEnabled(CrashModes.T_TWO)){
            return transaction.checkForCommit_T_TWO();
        // TransactionManager crash mode 3
        } else if (this.chaosMonkey.checkIfEnabled(CrashModes.T_THREE)) {
            return transaction.checkForCommit_T_THREE();
        // TransactionManager crash mode 4
        } else if (this.chaosMonkey.checkIfEnabled(CrashModes.T_FOUR)) {
            return transaction.checkForCommit_T_FOUR();
        } else {
            return transaction.checkForCommit();
        }
    }
}
