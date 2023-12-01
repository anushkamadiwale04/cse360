import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class PasswordSecurity {

    // Method to hash a password and store it in a text file
    public static void hashPasswordAndStore(String password) {
        try {
            String hashedPassword = hashPassword(password);
            storeHashedPassword(hashedPassword);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    // Hashes the password using SHA-256
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    // Stores the hashed password in a text file
    private static void storeHashedPassword(String hashedPassword) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("hashed_passwords.txt", true))) {
            writer.write(hashedPassword + System.lineSeparator());
        }
    }
}
