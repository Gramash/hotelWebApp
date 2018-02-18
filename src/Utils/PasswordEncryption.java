package Utils;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncryption {
    // The higher the number of iterations the more
    // expensive computing the hash is for us and
    // also for an attacker.
    private static final int iterations = 20 * 1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    public static String getSaltedHash(String password) throws NoSuchAlgorithmException {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        return Base64.getEncoder().encodeToString(salt) + "$" + hash(password, salt);
    }

    public static boolean check(String password, String stored)  {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            return false;
        }
        String hashOfInput = hash(password, Base64.getDecoder().decode(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    public static String hash(String password, byte[] salt) {
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            SecretKey key = null;
            key = f.generateSecret(new PBEKeySpec(
                    password.toCharArray(), salt, iterations, desiredKeyLen));
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (GeneralSecurityException e) {
            System.out.println("failed to hash pass");
            e.printStackTrace();
        }
        return "";
    }
}

