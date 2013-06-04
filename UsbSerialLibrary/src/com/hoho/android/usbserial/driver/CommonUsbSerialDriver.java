/* Copyright 2013 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: http://code.google.com/p/usb-serial-for-android/
 */

package com.hoho.android.usbserial.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.io.IOException;
import java.security.AccessControlException;
import java.util.Arrays;

/**
 * A base class shared by several driver implementations.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
abstract class CommonUsbSerialDriver implements UsbSerialDriver {

    public static final int DEFAULT_WRITE_TIMEOUT = 1000;

    protected final UsbDevice mDevice;
    protected UsbDeviceConnection mConnection;

    public CommonUsbSerialDriver(UsbDevice device) {
        mDevice = device;
    }

    @Override
    public UsbDevice getDevice() {
        return mDevice;
    }

    @Override
    public void open(UsbManager usbManager) throws IOException, AccessControlException {
        if (!usbManager.hasPermission(mDevice)) {
            throw new AccessControlException("No permission to access USB device " + mDevice);
        }
        mConnection = usbManager.openDevice(mDevice);
        if (mConnection == null) {
            throw new IOException("Could not open USB device " + mDevice);
        }
    }

    @Override
    public abstract void close() throws IOException;

    @Override
    public int getPortCount() {
        return 1;
    }

    @Override
    public UsbSerialPort getPort(int i) {
        if (i == 0) {
            return this;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public abstract int read(final byte[] dest, final int timeoutMillis) throws IOException;

    @Override
    public void write(final byte[] src) throws IOException {
        int count = 0;
        byte[] buffer = src;
        int bufferPayloadLength = src.length;

        // If not all of the data could be transferred with one step,
        // try again until all bytes are transferred.
        while (count < src.length) {
            int writeRet = write(buffer, bufferPayloadLength, DEFAULT_WRITE_TIMEOUT);
            if (writeRet == 0) {
                // The timeout should be large enough that at least some
                // bytes should have been transferred, so throw an exception
                // if not
                throw new IOException("Could not write data to device");
            } else if (writeRet != bufferPayloadLength) {
                if (src == buffer) {
                    // In the next transfer steps, we need to use a temporary array
                    // copy of src, because bulkTransfer does not support offsets
                    // and moving bytes in src might a bad idea, maybe the
                    // caller does not want src to be modified.
                    buffer = Arrays.copyOfRange(src,
                            writeRet,
                            src.length);
                    bufferPayloadLength = buffer.length; 
                } else {
                    // We already use a temporary array copy of src, so
                    // we can move the non transferred data to the beginning
                    // of the array.
                    bufferPayloadLength -= writeRet;
                    System.arraycopy(buffer,
                            writeRet,
                            buffer,
                            0,
                            bufferPayloadLength);
                }
            }
            count += writeRet;
        }
    }

    @Override
    public abstract int write(final byte[] src,
            final int length,
            final int timeoutMillis) throws IOException;

    @Override
    public abstract void setParameters(
            int baudRate, int dataBits, int stopBits, int parity) throws IOException;

    @Override
    public abstract boolean getCD() throws IOException;

    @Override
    public abstract boolean getCTS() throws IOException;

    @Override
    public abstract boolean getDSR() throws IOException;

    @Override
    public abstract boolean getDTR() throws IOException;

    @Override
    public abstract void setDTR(boolean value) throws IOException;

    @Override
    public abstract boolean getRI() throws IOException;

    @Override
    public abstract boolean getRTS() throws IOException;

    @Override
    public abstract void setRTS(boolean value) throws IOException;

    @Override
    public boolean purgeHwBuffers(boolean flushReadBuffers, boolean flushWriteBuffers) throws IOException {
        return !flushReadBuffers && !flushWriteBuffers;
    }

}
