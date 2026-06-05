package com.sadman.drs.client.bridge;

import com.sadman.drs.protocol.ServerRequest;
import com.sadman.drs.protocol.ServerResponse;
import com.sadman.drs.server.config.ServerConfig;
import com.sadman.drs.security.CryptoUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A minimal client abstraction for connecting to the DRS server.
 */
public class DRSClient implements AutoCloseable {

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
        socket = new Socket(host, port);
        Cipher encryptCipher = CryptoUtils.createCipher(Cipher.ENCRYPT_MODE, ServerConfig.ENCRYPTION_KEY);
        Cipher decryptCipher = CryptoUtils.createCipher(Cipher.DECRYPT_MODE, ServerConfig.ENCRYPTION_KEY);
        outputStream = new ObjectOutputStream(new CipherOutputStream(socket.getOutputStream(), encryptCipher));
        inputStream = new ObjectInputStream(new CipherInputStream(socket.getInputStream(), decryptCipher));
    }

    public ServerResponse sendRequest(ServerRequest request) throws IOException, ClassNotFoundException {
        if (socket == null || socket.isClosed()) {
            throw new IllegalStateException("Client is not connected to the server.");
        }
        outputStream.writeObject(request);
        outputStream.flush();
        return (ServerResponse) inputStream.readObject();
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
