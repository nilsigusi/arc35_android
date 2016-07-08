package com.quadriq.audionfc;

/**
 * Created by nilsen on 01.07.16.
 */
public interface Acr3xNotifListener {
    public void onUUIDAavailable(String uuid);
    public void onFirmwareVersionAvailable(String firmwareVersion);
}
