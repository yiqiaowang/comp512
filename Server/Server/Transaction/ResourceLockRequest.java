package Server.Transaction;

import static Server.LockManager.TransactionLockObject.LockType;

public class ResourceLockRequest {
    private final String resourceName;
    private final LockType lockType;

    public ResourceLockRequest(String resourceName, LockType lockType) {
        this.resourceName = resourceName;
        this.lockType = lockType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public LockType getLockType() {
        return lockType;
    }
}
