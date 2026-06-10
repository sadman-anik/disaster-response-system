package com.sadman.drs.client.bridge;

import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.server.config.ServerConfig;
import com.sadman.drs.security.CryptoUtils;

import javax.crypto.SealedObject;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A minimal client abstraction for connecting to the DRS server.
 */
public class DRSClient implements AutoCloseable {

    private static final int CONNECT_TIMEOUT_MILLIS = 3000;

    private final String host;
    private final int port;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public DRSClient() {
        this(ServerConfig.HOST, ServerConfig.PORT);
    }

    public DRSClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        Socket newSocket = new Socket();
        try {
            newSocket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT_MILLIS);
            ObjectOutputStream newOutputStream = new ObjectOutputStream(newSocket.getOutputStream());
            newOutputStream.flush();
            ObjectInputStream newInputStream = new ObjectInputStream(newSocket.getInputStream());
            socket = newSocket;
            outputStream = newOutputStream;
            inputStream = newInputStream;
        } catch (IOException | RuntimeException exception) {
            try {
                newSocket.close();
            } catch (IOException ignored) {
            }
            throw exception;
        }
    }

    public ServerResponse sendRequest(ServerRequest request) throws IOException, ClassNotFoundException {
        if (socket == null || socket.isClosed()) {
            throw new IllegalStateException("Client is not connected to the server.");
        }
        outputStream.writeObject(CryptoUtils.seal(request, ServerConfig.ENCRYPTION_KEY));
        outputStream.flush();
        return (ServerResponse) CryptoUtils.unseal((SealedObject) inputStream.readObject(), ServerConfig.ENCRYPTION_KEY);
    }

    @Override
    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ignored) {
        }
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ignored) {
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }
}
