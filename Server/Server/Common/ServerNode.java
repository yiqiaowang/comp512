package Server.Common;

public class ServerNode {
    private final String host;
    private final int port;
    private final String name;

    public ServerNode(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerNode that = (ServerNode) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServerNode{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }
}
