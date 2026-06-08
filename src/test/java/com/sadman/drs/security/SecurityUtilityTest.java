package com.sadman.drs.security;

import com.sadman.drs.server.config.ServerConfig;
import com.sadman.drs.server.repository.UserRepository;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SecurityUtilityTest {

    @Test
    void passwordHashShouldBeDeterministicAndNotStorePlainText() {
        String password = "Reporter@123";

        String firstHash = UserRepository.hashPassword(password);
        String secondHash = UserRepository.hashPassword(password);

        assertEquals(firstHash, secondHash);
        assertNotEquals(password, firstHash);
        assertFalse(firstHash.isBlank());
        Base64.getDecoder().decode(firstHash);
    }

    @Test
    void cryptoUtilsShouldEncryptAndDecryptTransportPayload() throws Exception {
        String message = "DRS confidential disaster payload";
        Cipher encryptCipher = CryptoUtils.createCipher(Cipher.ENCRYPT_MODE, ServerConfig.ENCRYPTION_KEY);
        Cipher decryptCipher = CryptoUtils.createCipher(Cipher.DECRYPT_MODE, ServerConfig.ENCRYPTION_KEY);

        byte[] encrypted = encryptCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        byte[] decrypted = decryptCipher.doFinal(encrypted);

        assertNotEquals(message, new String(encrypted, StandardCharsets.UTF_8));
        assertEquals(message, new String(decrypted, StandardCharsets.UTF_8));
    }
}
