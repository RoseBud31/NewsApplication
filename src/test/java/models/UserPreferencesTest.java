package models;

import com.example.newsapplicationversion1.models.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPreferencesTest {
    private UserPreferences userPreferences;
    private static final int TEST_PREFERENCE_ID = 1;
    private static final int TEST_USER_ID = 100;
    private static final List<String> TEST_CATEGORIES = Arrays.asList("Technology", "Science", "Health");

    @BeforeEach
    void setUp() {
        userPreferences = new UserPreferences();
        userPreferences.setPreferenceId(TEST_PREFERENCE_ID);
        userPreferences.setUserId(TEST_USER_ID);
        userPreferences.setPreferredCategories(TEST_CATEGORIES);
    }

    @Test
    void testSettersAndGetters() {
        // Preference ID
        assertEquals(TEST_PREFERENCE_ID, userPreferences.getPreferenceId());

        // User ID
        assertEquals(TEST_USER_ID, userPreferences.getUserId());

        // Preferred Categories
        assertIterableEquals(TEST_CATEGORIES, userPreferences.getPreferredCategories());
    }

    @Test
    void testSetPreferenceId() {
        int newPreferenceId = 2;
        userPreferences.setPreferenceId(newPreferenceId);
        assertEquals(newPreferenceId, userPreferences.getPreferenceId());
    }

    @Test
    void testSetUserId() {
        int newUserId = 200;
        userPreferences.setUserId(newUserId);
        assertEquals(newUserId, userPreferences.getUserId());
    }

    @Test
    void testSetPreferredCategories() {
        List<String> newCategories = Arrays.asList("Sports", "Entertainment");
        userPreferences.setPreferredCategories(newCategories);
        assertIterableEquals(newCategories, userPreferences.getPreferredCategories());
    }

    @Test
    void testEmptyPreferredCategories() {
        UserPreferences emptyPreferences = new UserPreferences();
        emptyPreferences.setPreferredCategories(null);
        assertNull(emptyPreferences.getPreferredCategories());
    }

    @Test
    void testMultiplePreferenceUpdates() {
        // Multiple updates to verify consistent behavior
        userPreferences.setPreferenceId(3);
        userPreferences.setUserId(300);
        List<String> updatedCategories = Arrays.asList("Health", "Education");
        userPreferences.setPreferredCategories(updatedCategories);

        assertEquals(3, userPreferences.getPreferenceId());
        assertEquals(300, userPreferences.getUserId());
        assertIterableEquals(updatedCategories, userPreferences.getPreferredCategories());
    }
}
