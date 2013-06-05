package com.hoho.android.usbserial.driver;

import java.io.IOException;
import java.security.AccessControlException;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

public abstract class CommonSinglePortUsbSerialDriver extends CommonUsbSerialPort implements UsbSerialDriver {

    protected final UsbDevice mDevice;
    protected UsbDeviceConnection mConnection;

    public CommonSinglePortUsbSerialDriver(UsbDevice device) {
        mDevice = device;
    }

    @Override
    public void open(UsbManager usbManager) throws IOException, AccessControlException {
        mConnection = CommonUsbSerialDriver.open(usbManager, mDevice);
    }

    @Override
    public void close() throws IOException {
        mConnection.close();
    }

    @Override
    public UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public int getPortCount() {
        return 1;
    }

    @Override
    public UsbSerialPort getPort(int i) throws IndexOutOfBoundsException {
        if (i == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

}
