package org.example.icatch.Camera;

public class WifiCredentials {
    private String wifiId;
    private String wifiPassword;

    public WifiCredentials() {}

    public WifiCredentials(String wifiId, String wifiPassword) {
        this.wifiId = wifiId;
        this.wifiPassword = wifiPassword;
    }

    public String getWifiId() {
        return wifiId;
    }

    public void setWifiId(String wifiId) {
        this.wifiId = wifiId;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }
}