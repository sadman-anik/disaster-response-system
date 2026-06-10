package com.sadman.drs.security;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * Utility class for encrypting and decrypting client/server transport payloads.
 */
public final class CryptoUtils {
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "AES";
    private static final String INIT_VECTOR = "DRSInitVector123";

    private CryptoUtils() {
    }

    public static Cipher createCipher(int mode, String secretKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(mode, keySpec, ivSpec);
            return cipher;
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to initialize cipher", exception);
        }
    }

    public static SealedObject seal(Serializable object, String secretKey) throws IOException {
        try {
            return new SealedObject(object, createCipher(Cipher.ENCRYPT_MODE, secretKey));
        } catch (GeneralSecurityException exception) {
            throw new IOException("Unable to encrypt transport object.", exception);
        }
    }

    public static Object unseal(SealedObject object, String secretKey) throws IOException, ClassNotFoundException {
        try {
            return object.getObject(createCipher(Cipher.DECRYPT_MODE, secretKey));
        } catch (GeneralSecurityException exception) {
            throw new IOException("Unable to decrypt transport object.", exception);
        }
    }
}
