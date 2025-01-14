package BaseballQuant.Model;

public class ScrapingProxy {
    private String IP;
    private Integer port;

    public ScrapingProxy(String IP, Integer port) {
        this.IP = IP;
        this.port = port;
    }
    public ScrapingProxy() {
    }
    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "ScrapingProxy{" +
                "IP='" + IP + '\'' +
                ", port=" + port +
                '}';
    }
}
