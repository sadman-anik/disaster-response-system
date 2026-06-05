package com.sadman.drs.server;

import com.sadman.drs.protocol.ServerAction;
import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.server.DRSServerRequestProcessor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Handles an individual client connection to the DRS server.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DRSServerRequestProcessor requestProcessor = new DRSServerRequestProcessor();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

            while (!clientSocket.isClosed()) {
                try {
                    ServerRequest request = (ServerRequest) inputStream.readObject();
                    ServerResponse response = requestProcessor.processRequest(request);
                    outputStream.writeObject(response);
                    outputStream.flush();
                } catch (EOFException eofException) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            System.err.println("Client handler error: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
