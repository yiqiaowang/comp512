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
    private boolean isRunning = false;
    private IResourceManager middleware;

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public boolean hasFailure() {
        return this.detectedFailure;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void setServer(String server) {
        this.server = server; 
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setMiddleware(IResourceManager middleware) {
        this.middleware = middleware;
    }

    public void run() {
        this.isRunning = true;
        while (true) {
            try {
                Thread.sleep(5000);
                if (System.currentTimeMillis() > middlewareStatus.getTTL()) {
                    // if the `isAlive' call fails, we go to the first catch block
                    middleware.isAlive();
                    middlewareStatus.setTTL(System.currentTimeMillis() + 5000);
                } else {
                    System.out.println("Health checks skipped!");  
                }
            } catch(RemoteException e) {
                this.detectedFailure = true;
                // this.isEnabled = false;
                System.out.println("Failure suspected!");
                this.isRunning = false;
                return;
            } catch(InterruptedException e) {
                return;
            }
        }
    }
} 
