package com.hoho.android.usbserial.driver;

import java.io.IOException;
import java.security.AccessControlException;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

public abstract class CommonMultiPortUsbSerialDriver extends CommonUsbSerialDriver {

    private final UsbSerialPort mPorts[];

    private int mOpenPortsCount = 0;

    public CommonMultiPortUsbSerialDriver(UsbDevice device,
            int portCount) {
        super(device);
        mPorts = new UsbSerialPort[portCount];
    }

    @Override
    protected void open(UsbManager usbManager) throws IOException,
            AccessControlException {
        if (mOpenPortsCount == 0) {
            super.open(usbManager);
        }
        ++mOpenPortsCount;
    }

    protected void close() {
        --mOpenPortsCount;
        if (mOpenPortsCount == 0) {
            mConnection.close();
        }
    }

    abstract protected UsbSerialPort createPortInstance(int portIdx);

    @Override
    public int getPortCount() {
        return mPorts.length;
    }

    @Override
    public UsbSerialPort getPort(int i) throws IndexOutOfBoundsException {
        if ((i >= 0) && i < (mPorts.length)) {
            UsbSerialPort port = mPorts[i];
            if (port == null) {
                port = createPortInstance(i);
                mPorts[i] = port;
            }
            return port;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

}
