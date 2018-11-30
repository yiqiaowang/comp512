package Server.Transaction;

import Server.Common.ChaosMonkey;
import Server.Common.CrashModes;
import Server.Common.SerializableLock;
import Server.Interface.InvalidTransactionException;
import Server.LockManager.DeadlockException;
import Server.LockManager.LockManager;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TransactionManager implements Serializable {

    public ChaosMonkey chaosMonkey;
    private Map<Integer, Transaction> transactions;
    private AtomicInteger transactionIdGenerator;
    private LockManager lockManager;

    private Set<Integer> committedTransactions;
    private Set<Integer> abortedTransactions;

    private static final String MIDDLEWARE_TRANSACTION_DATA = "./middleware_transaction_data";
    private static final String whichRecordPath = "./middleware_record_name";

    private transient volatile int masterSuffix = 0;
    private transient Map<Integer, SerializableLock> transactionLocks;


    private static int findMaster() {
        File whichRecord = new File(whichRecordPath);
        if (whichRecord.exists()) {
            try (InputStream input = new FileInputStream(whichRecord)) {
                return input.read() - '0';
            } catch (IOException ignored) { }
        }

        return 0;
    }

    private void startCheckForTimeouts() {

        // Crash mode 8
        // can't even disable this without deleting the backup file?
        this.chaosMonkey.crashIfEnabled(CrashModes.T_EIGHT);

        Thread checkForTimeouts = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }

                List<Transaction> timedOutTransactions = new ArrayList<>();

                for (Transaction transaction : transactions.values()) {
                    if (transaction.checkForTimeout()) {
                        timedOutTransactions.add(transaction);
                    }
                }

                timedOutTransactions.sort(Comparator.comparing(transaction -> transaction.lastOperationTimestamp.get()));


                for (Transaction timedOutTransaction : timedOutTransactions) {
                    System.out.println("Transaction " + timedOutTransaction.getTransactionId() + " has timed out");
                    abort(timedOutTransaction.getTransactionId());
                }

                if (timedOutTransactions.size() > 0) {
                    persistData();
                }
            }
        });
        checkForTimeouts.start();
    }


    public static TransactionManager create() {

        int suffix = findMaster();

        for (int fileSuffix: new int[] {suffix, (suffix + 1) % 2}) {
            try {
                TransactionManager transactionManager = initialize(new File(filePath(fileSuffix)));
                transactionManager.masterSuffix = fileSuffix;
                transactionManager.transactionLocks = new WeakHashMap<>();
                transactionManager.startCheckForTimeouts();
                System.out.println("Recovered transaction manager from disk.");
                return transactionManager;
            } catch (IOException | ClassNotFoundException ignored) { }
        }

        System.out.println("Created new transaction manager.");
        return createBlankTransaction();
    }

    private static TransactionManager createBlankTransaction() {
        TransactionManager transactionManager = new TransactionManager();
        transactionManager.lockManager = new LockManager();
        transactionManager.transactionIdGenerator = new AtomicInteger(0);
        transactionManager.transactions = new ConcurrentHashMap<>();
        transactionManager.chaosMonkey = new ChaosMonkey();
        transactionManager.committedTransactions = ConcurrentHashMap.newKeySet();
        transactionManager.abortedTransactions = ConcurrentHashMap.newKeySet();
        transactionManager.transactionLocks = new WeakHashMap<>();

        transactionManager.startCheckForTimeouts();

        return transactionManager;
    }


    private static TransactionManager initialize(File fileToInitializeFrom) throws IOException, ClassNotFoundException {
        TransactionManager transactionManager;

        try (
                InputStream file = new FileInputStream(fileToInitializeFrom);
                ObjectInputStream inputStream = new ObjectInputStream(file)
        ) {
            transactionManager = (TransactionManager) inputStream.readObject();
        }


        return transactionManager;
    }


    public int startTransaction() {
        int transactionId = transactionIdGenerator.incrementAndGet();
        Transaction transaction = new Transaction(transactionId);
        transactions.put(transactionId, transaction);

        persistData();

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

        synchronized (getTransactionLock(transactionId))
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
                persistData();
            }

            return true;
        }
    }

    public boolean commit(int transactionId) throws RemoteException {
        Transaction transaction = transactions.get(transactionId);
        boolean commitResult;

        if (transaction == null) {
            return false;
        }
        // Commit has been decided already in prepare!
        //
        // Crash mode 5 at the transaction manager
        this.chaosMonkey.crashIfEnabled(CrashModes.T_FIVE);

        // Crash mode 6 at the transaction manager
        if (this.chaosMonkey.checkIfEnabled(CrashModes.T_SIX)) {
            commitResult = transaction.commit_fail();
        }

        // Sends the commit to resource managers
        commitResult = transaction.commit();
        transactions.remove(transactionId);
        lockManager.UnlockAll(transactionId);

        persistData();

        // crash mode 7
        if (chaosMonkey.checkIfEnabled(CrashModes.T_SEVEN)) {
            System.out.println("Crashing due to crash mode seven");
        }

        this.chaosMonkey.crashIfEnabled(CrashModes.T_SEVEN);

        return commitResult;
    }

    public void abort(int transactionId) {
        Transaction transaction = transactions.get(transactionId);

        if (transaction == null) {
            return;
        }

        abortedTransactions.add(transactionId);

        transaction.abort();
        transactions.remove(transactionId);
        lockManager.UnlockAll(transactionId);

        persistData();
    }

    public boolean isOngoingTransaction(int transactionId) {
        return transactions.containsKey(transactionId);
    }

    public boolean startTransaction(int transactionId) {
        if (!isOngoingTransaction(transactionId)) {
            Transaction transaction = new Transaction(transactionId);
            transactions.put(transactionId, transaction);

            persistData();
            return true;
        } else {
            return false;
        }
    }


    private static String filePath(int suffix) {
        return MIDDLEWARE_TRANSACTION_DATA + "_" + suffix;
    }

    public synchronized void persistData() {
        try (
                OutputStream file = new FileOutputStream(filePath((masterSuffix + 1) % 2));
                ObjectOutputStream outputStream = new ObjectOutputStream(file)
        ) {
            outputStream.writeObject(this);
            masterSuffix = (masterSuffix + 1) % 2;
            try (OutputStream output = new FileOutputStream(whichRecordPath)) {
                output.write(masterSuffix + '0');
                System.out.println("Successfully persisted data and updated write pointer.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean prepare(int transactionId) throws RemoteException, InvalidTransactionException {
        boolean decision;

        // TransactionManager crash mode 1
        this.chaosMonkey.crashIfEnabled(CrashModes.T_ONE);

        Transaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new InvalidTransactionException(transactionId);
        }

        // TransactionManager crash mode 2
        if (this.chaosMonkey.checkIfEnabled(CrashModes.T_TWO)){
            decision = transaction.checkForCommit_T_TWO();
        // TransactionManager crash mode 3
        } else if (this.chaosMonkey.checkIfEnabled(CrashModes.T_THREE)) {
            decision = transaction.checkForCommit_T_THREE();
        // TransactionManager crash mode 4
        } else if (this.chaosMonkey.checkIfEnabled(CrashModes.T_FOUR)) {
            decision = transaction.checkForCommit_T_FOUR();
        } else {
            decision = transaction.checkForCommit();
        }

        if (decision) {
            committedTransactions.add(transactionId);
        } else {
            abortedTransactions.add(transactionId);
        }

        persistData();

        return decision;
    }

    public Set<Integer> getCommittedTransactions() {
        return committedTransactions;
    }

    public Set<Integer> getAbortedTransactions() {
        return abortedTransactions;
    }

    public SerializableLock getTransactionLock(int transactionId) {
        return transactionLocks.computeIfAbsent(transactionId, xid -> new SerializableLock());
    }
}
