package other;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.SecureRandom;

public class Crypto {

    private static final String KEYSTORE_FILE = "keystore.jks";
    private static final String KEYSTORE_PASSWORD = "very_strong";
    private static final String ALIAS = "key_for_cipher_passwords";
    private static final String KEY_PASSWORD = "very_very";
    Cipher cipher;
    SecretKey key;

    public Crypto() {
        try {
            if (!Files.exists(Paths.get(KEYSTORE_FILE))) {
                createKeyStore();
            }

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] keystorePassword = KEYSTORE_PASSWORD.toCharArray();
            try (FileInputStream fis = new FileInputStream(KEYSTORE_FILE)) {
                keyStore.load(fis, keystorePassword);
            }

            if (keyStore.containsAlias(ALIAS)) {
                key = (SecretKey) keyStore.getKey(ALIAS, KEY_PASSWORD.toCharArray());
            } else {
                SecureRandom random = new SecureRandom();
                byte[] keyBytes = new byte[16];
                random.nextBytes(keyBytes);
                key = new SecretKeySpec(keyBytes, "AES");
                keyStore.setKeyEntry(ALIAS, key, KEY_PASSWORD.toCharArray(), null);
                try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
                    keyStore.store(fos, keystorePassword);
                }
            }
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createKeyStore() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] keystorePassword = KEYSTORE_PASSWORD.toCharArray();
        keyStore.load(null, keystorePassword);
        try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE)) {
            keyStore.store(fos, keystorePassword);
        }
    }

    public String encrypt(String password){
        byte[] codedPswByte;
        String codedPswStr;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            codedPswByte = cipher.doFinal(password.getBytes());
            codedPswStr = java.util.Base64.getEncoder().encodeToString(codedPswByte);
        }
        catch (Exception e){
            System.out.println("ошибка шифрации");
            codedPswStr = null;
        }
        return codedPswStr;
    }

    public String decrypt(String codedPswStr){
        byte[] codedPswByte = java.util.Base64.getDecoder().decode(codedPswStr);
        String decodedPsw;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            decodedPsw = new String(cipher.doFinal(codedPswByte));
        }
        catch (Exception e){
            System.out.println("ошибка дешифрации");
            decodedPsw = null;
        }
        return decodedPsw;
    }
}
