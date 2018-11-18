package Server.Common;

public class PeerStatus {
    private boolean alive = true;
    private long ttl = 0;
    private boolean stale = true;

    public long getTTL() {
        return this.ttl;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public boolean isStale() {
        return this.stale;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl; 
    }

    public void setAlive(boolean isAlive) {
        this.alive = isAlive;
    }

    public void setStale(boolean isStale) {
        this.stale = isStale;
    }
}
