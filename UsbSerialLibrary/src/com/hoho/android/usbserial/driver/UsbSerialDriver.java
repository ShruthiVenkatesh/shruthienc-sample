/* Copyright 2011 Google Inc.
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

import java.io.IOException;
import java.security.AccessControlException;

import android.hardware.usb.UsbManager;

/**
 * Driver interface for a USB serial device.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public interface UsbSerialDriver extends UsbSerialPort {

    /**
     * Opens and initializes the device as a USB serial device. Upon success,
     * caller must ensure that {@link #close()} is eventually called.
     *
     * @param usbManager the {@link UsbManager} to use.
     * @throws IOException on error opening or initializing the device.
     * @throws AccessControlException the permission to access the USB device is not granted.
     */
    public void open(UsbManager usbManager) throws IOException, AccessControlException;

    /**
     * Closes the serial device.
     *
     * @throws IOException on error closing the device.
     */
    public void close() throws IOException;

    /**
     * Get the count of available ports for this USB device.
     * 
     * @return count of available ports.
     */
    public int getPortCount();

    /** 
     * Get port.
     * 
     * @param i index of the port.
     * @return {@link UsbSerialPort} the port with index i
     * @throws IndexOutOfBoundsException No port with index i exists.
     */
    public UsbSerialPort getPort(int i) throws IndexOutOfBoundsException;

}
