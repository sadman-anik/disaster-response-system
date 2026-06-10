package com.sadman.drs.server;

import com.sadman.drs.model.User;
import com.sadman.drs.protocol.ServerAction;
import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.security.CryptoUtils;
import com.sadman.drs.server.config.ServerConfig;

import javax.crypto.SealedObject;
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
    private User authenticatedUser;
    private final DRSServerRequestProcessor requestProcessor = new DRSServerRequestProcessor();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    private void setAuthenticatedUser(User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    @Override
    public void run() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {
            outputStream.flush();
            try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {

                while (!clientSocket.isClosed()) {
                    try {
                        ServerRequest request = (ServerRequest) CryptoUtils.unseal(
                                (SealedObject) inputStream.readObject(), ServerConfig.ENCRYPTION_KEY);
                        ServerResponse response = requestProcessor.processRequest(request, authenticatedUser);
                        if (request.getAction() == ServerAction.AUTHENTICATE && response.isSuccess() && response.getPayload() instanceof User user) {
                            setAuthenticatedUser(user);
                        }
                        outputStream.writeObject(CryptoUtils.seal(response, ServerConfig.ENCRYPTION_KEY));
                        outputStream.flush();
                    } catch (EOFException eofException) {
                        break;
                    }
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
