package com.hoho.android.usbserial.driver;

import java.io.IOException;
import java.util.Arrays;

public abstract class CommonUsbSerialPort implements UsbSerialPort {

    public static final int DEFAULT_WRITE_TIMEOUT = 1000;

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
    public boolean purgeHwBuffers(boolean flushReadBuffers, boolean flushWriteBuffers) throws IOException {
        return !flushReadBuffers && !flushWriteBuffers;
    }

}
