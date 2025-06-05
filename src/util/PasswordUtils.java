package util;

public class PasswordUtils {
    public static String encrypt(String password) {
        StringBuilder sb = new StringBuilder();
        for (char c : password.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append((char) (c + 3));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String decrypt(String encrypted) {
        StringBuilder sb = new StringBuilder();
        for (char c : encrypted.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append((char) (c - 3));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

