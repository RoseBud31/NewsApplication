package models;

import com.example.newsapplicationversion1.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;
    private static final int TEST_USER_ID = 1;
    private static final String TEST_FIRST_NAME = "John";
    private static final String TEST_LAST_NAME = "Doe";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_ROLE = "USER";

    @BeforeEach
    void setUp() {
        user = new User(
                TEST_USER_ID,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_ROLE
        );
    }

    @Test
    void testConstructor() {
        assertEquals(TEST_USER_ID, user.getUserId());
        assertEquals(TEST_FIRST_NAME, user.getFirstName());
        assertEquals(TEST_LAST_NAME, user.getLastName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(TEST_PASSWORD, user.getPassword());
        assertEquals(TEST_ROLE, user.getRole());

        // Check that createdAt and lastLogin are set to current time
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getLastLogin());
    }

    @Test
    void testSetters() {
        // Test each setter method
        user.setUserId(2);
        assertEquals(2, user.getUserId());

        user.setFirstName("Jane");
        assertEquals("Jane", user.getFirstName());

        user.setLastName("Smith");
        assertEquals("Smith", user.getLastName());

        user.setEmail("jane.smith@example.com");
        assertEquals("jane.smith@example.com", user.getEmail());

        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());

        user.setRole("ADMIN");
        assertEquals("ADMIN", user.getRole());
    }

    @Test
    void testLoginSuccessful() {
        // Test successful login
        Date initialLastLogin = user.getLastLogin();

        // Wait a moment to ensure lastLogin timestamp changes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean loginResult = user.login(TEST_EMAIL, TEST_PASSWORD);

        assertTrue(loginResult);
        // Verify that lastLogin has been updated
        assertNotEquals(initialLastLogin, user.getLastLogin());
    }

    @Test
    void testLoginFailure() {
        // Test login with incorrect email
        assertFalse(user.login("wrong@email.com", TEST_PASSWORD));

        // Test login with incorrect password
        assertFalse(user.login(TEST_EMAIL, "wrongpassword"));

        // Test login with both incorrect email and password
        assertFalse(user.login("wrong@email.com", "wrongpassword"));
    }

    @Test
    void testCreatedAtImmutability() {
        // Verify that createdAt cannot be modified
        Date originalCreatedAt = user.getCreatedAt();

        // Successful login should not modify createdAt
        user.login(TEST_EMAIL, TEST_PASSWORD);
        assertEquals(originalCreatedAt, user.getCreatedAt());
    }
}