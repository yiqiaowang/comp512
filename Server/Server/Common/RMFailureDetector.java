package Server.Common;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import Server.Interface.IResourceManager;

class RMFailureDetector implements Runnable {
    private static String s_rmiPrefix = "groupFive_";
    private PeerStatus middlewareStatus = new PeerStatus();
    private String server;
    private int port;
    private boolean isEnabled = true;
    private boolean detectedFailure = false;
    private IResourceManager middleware;


    public RMFailureDetector(String server, int port) { 
        this.server = server;
        this.port = port;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public boolean hasFailure() {
        return this.detectedFailure;
    }

    public void run() {
        try {
            Registry registry = LocateRegistry.getRegistry(server, port);
            middleware = (IResourceManager)registry.lookup(s_rmiPrefix + "Middleware");
        } catch(NotBoundException | RemoteException e){
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(2000);
                if (this.isEnabled && System.currentTimeMillis() > middlewareStatus.getTTL()) {
                    // if the `isAlive' call fails, we go to the first catch block
                    middleware.isAlive();
                    middlewareStatus.setTTL(System.currentTimeMillis() + 1000);
                    System.out.println("Health checks passed!");
                } else {
                    System.out.println("Health checks skipped!");  
                }
            } catch(RemoteException e) {
                this.detectedFailure = true;
                this.isEnabled = false;
                System.out.println("Failure suspected!");
            } catch(InterruptedException e) {
                return;
            }
        }
    }
} 
