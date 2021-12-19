/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.port;

import de.elbe5.base.log.Log;
import de.elbe5.base.thread.BaseThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PortListener extends BaseThread {

    protected ServerSocket listener = null;
    protected int port;
    protected IPortObjectProcessor processor;

    public PortListener(String name, int port, IPortObjectProcessor processor) {
        super(name);
        this.port = port;
        this.processor = processor;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void startRunning() {
        listener = openServerSocket();
        super.startRunning();
    }

    @Override
    public void stopRunning() {
        running = false;
        if (listener != null) {
            try {
                listener.close();
            } catch (IOException ignore) {
            }
            listener = null;
        }
    }

    protected ServerSocket openServerSocket() {
        ServerSocket socket = null;
        while (socket == null) {
            try {
                socket = new ServerSocket(port);
            } catch (IOException e) {
                port++;
            }
        }
        return socket;
    }

    @Override
    public void run() {
        Log.info("port listener started on port " + port);
        while (running) {
            try (Socket server = listener.accept()) {
                ObjectInputStream obj_in = new ObjectInputStream(server.getInputStream());
                Object obj = obj_in.readObject();
                if (obj == null) {
                    break;
                }
                obj = processor.processObject(obj);
                if (obj != null) {
                    ObjectOutputStream obj_out = new ObjectOutputStream(server.getOutputStream());
                    obj_out.writeObject(obj);
                    obj_out.flush();
                }
            } catch (Exception e) {
                if (running) {
                    Log.error("could not process object", e);
                }
            }
        }
        Log.info("port listener stopped");
        running = false;
    }
}
