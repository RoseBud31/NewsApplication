package models;

import com.example.newsapplicationversion1.models.UserArticleInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class UserArticleInteractionTest {
    private UserArticleInteraction interaction;
    private static final int TEST_INTERACTION_ID = 1;
    private static final int TEST_USER_ID = 100;
    private static final int TEST_ARTICLE_ID = 200;
    private static final String TEST_INTERACTION_TYPE = "READ";
    private static final int TEST_TIME_SPENT = 120;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        interaction = new UserArticleInteraction(
                TEST_INTERACTION_ID,
                TEST_USER_ID,
                TEST_ARTICLE_ID,
                TEST_INTERACTION_TYPE,
                TEST_TIME_SPENT,
                testDate
        );
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(TEST_INTERACTION_ID, interaction.getInteractionId());
        assertEquals(TEST_USER_ID, interaction.getUserId());
        assertEquals(TEST_ARTICLE_ID, interaction.getArticleId());
        assertEquals(TEST_INTERACTION_TYPE, interaction.getInteractionType());
        assertEquals(TEST_TIME_SPENT, interaction.getTimeSpentSeconds());
        assertEquals(testDate, interaction.getDateRead());
    }

    @Test
    void testSetters() {
        // Test Interaction ID
        interaction.setInteractionId(2);
        assertEquals(2, interaction.getInteractionId());

        // Test User ID
        interaction.setUserId(300);
        assertEquals(300, interaction.getUserId());

        // Test Article ID
        interaction.setArticleId(400);
        assertEquals(400, interaction.getArticleId());

        // Test Interaction Type
        interaction.setInteractionType("LIKED");
        assertEquals("LIKED", interaction.getInteractionType());

        // Test Time Spent
        interaction.setTimeSpentSeconds(240);
        assertEquals(240, interaction.getTimeSpentSeconds());

        // Test Date Read
        Date newDate = new Date(System.currentTimeMillis() + 86400000); // Tomorrow
        interaction.setDateRead(newDate);
        assertEquals(newDate, interaction.getDateRead());
    }

    @Test
    void testNegativeTimeSpent() {
        // Test setting negative time spent
        interaction.setTimeSpentSeconds(-30);
        assertEquals(0, interaction.getTimeSpentSeconds(),
                "Setter should allow negative time spent without validation");
    }

    @Test
    void testMultipleUpdates() {
        // Verify multiple updates work correctly
        interaction.setInteractionId(5);
        interaction.setUserId(500);
        interaction.setArticleId(600);
        interaction.setInteractionType("SHARED");
        interaction.setTimeSpentSeconds(300);
        Date multiUpdateDate = new Date();
        interaction.setDateRead(multiUpdateDate);

        assertEquals(5, interaction.getInteractionId());
        assertEquals(500, interaction.getUserId());
        assertEquals(600, interaction.getArticleId());
        assertEquals("SHARED", interaction.getInteractionType());
        assertEquals(300, interaction.getTimeSpentSeconds());
        assertEquals(multiUpdateDate, interaction.getDateRead());
    }

    @Test
    void testInteractionTypeVariety() {
        // Test different interaction types
        String[] interactionTypes = {"READ", "LIKED", "SHARED", "COMMENTED", "BOOKMARKED"};

        for (String type : interactionTypes) {
            interaction.setInteractionType(type);
            assertEquals(type, interaction.getInteractionType());
        }
    }
}
