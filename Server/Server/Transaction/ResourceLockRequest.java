package Server.Transaction;

import Server.Interface.IResourceManager;

import static Server.LockManager.TransactionLockObject.LockType;

public class ResourceLockRequest {
    private final String resourceName;
    private final IResourceManager resourceManager;
    private final LockType lockType;


    private ResourceLockRequest(String resourceName, IResourceManager resourceManager, LockType lockType) {
        this.resourceName = resourceName;
        this.resourceManager = resourceManager;
        this.lockType = lockType;
    }

    public ResourceLockRequest(String nodeName, Object uniqueData, IResourceManager resourceManager, LockType lockType) {
        this(nodeName + "-" + uniqueData, resourceManager, lockType);
    }

    public String getResourceName() {
        return resourceName;
    }

    public LockType getLockType() {
        return lockType;
    }

    public IResourceManager getResourceManager() {
        return resourceManager;
    }
}
