package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest {

    @Test
    public void testEncryptDecrypt() {
        String raw = "Ashwin123";
        String encrypted = PasswordUtils.encrypt(raw);
        String decrypted = PasswordUtils.decrypt(encrypted);

        assertEquals(raw, decrypted, "Decrypted password should match original");
    }

    @Test
    public void testEncryptIsNotSameAsRaw() {
        String raw = "Test@123";
        String encrypted = PasswordUtils.encrypt(raw);

        assertNotEquals(raw, encrypted, "Encrypted password should differ from raw password");
    }

    @Test
    public void testSpecialCharactersRemainUnchanged() {
        String input = "!@#$%^&*()";
        String encrypted = PasswordUtils.encrypt(input);
        String decrypted = PasswordUtils.decrypt(encrypted);

        assertEquals(input, decrypted, "Special characters should remain unchanged after encryption and decryption");
    }


    @Test
    public void testEmptyString() {
        String input = "";
        String encrypted = PasswordUtils.encrypt(input);
        String decrypted = PasswordUtils.decrypt(encrypted);

        assertEquals("", encrypted, "Encryption of empty string should be empty");
        assertEquals("", decrypted, "Decryption of empty string should be empty");
    }


    @Test
    public void testNumericOnlyPassword() {
        String input = "123456";
        String encrypted = PasswordUtils.encrypt(input);
        String decrypted = PasswordUtils.decrypt(encrypted);

        assertEquals(input, decrypted, "Numeric password should decrypt correctly");
    }


}
