package models;

import com.example.newsapplicationversion1.models.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {
    private Article article;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date(System.currentTimeMillis());
        article = new Article(
                1,
                "TestSource",
                "Test Title",
                "Test Author",
                "Technology",
                "Test Description",
                new java.sql.Date(System.currentTimeMillis()),
                "Test Content"
        );
    }

    @Test
    void testArticleConstructorWithParameters() {
        assertEquals(1, article.getArticleId());
        assertEquals("TestSource", article.getSource());
        assertEquals("Test Title", article.getTitle());
        assertEquals("Test Author", article.getAuthor());
        assertEquals("Technology", article.getCategory());
        assertEquals("Test Description", article.getDescription());
        assertEquals(testDate, article.getPublishedDate());
        assertEquals("Test Content", article.getContent());
        assertEquals("/com/example/newsapplicationversion1/images/1.jpg", article.getImageUrl());
    }

    @Test
    void testDefaultConstructor() {
        Article defaultArticle = new Article();

        assertEquals(0, defaultArticle.getArticleId());
        assertEquals("", defaultArticle.getSource());
        assertEquals("", defaultArticle.getTitle());
        assertEquals("", defaultArticle.getAuthor());
        assertEquals("", defaultArticle.getCategory());
        assertEquals("", defaultArticle.getDescription());
        assertNotNull(defaultArticle.getPublishedDate());
        assertEquals("", defaultArticle.getContent());
        assertNull(defaultArticle.getImageUrl());
    }

    @Test
    void testSetters() {
        article.setArticleId(2);
        assertEquals(2, article.getArticleId());

        article.setSource("New Source");
        assertEquals("New Source", article.getSource());

        article.setTitle("New Title");
        assertEquals("New Title", article.getTitle());

        article.setAuthor("New Author");
        assertEquals("New Author", article.getAuthor());

        article.setCategory("Sports");
        assertEquals("Sports", article.getCategory());

        article.setDescription("New Description");
        assertEquals("New Description", article.getDescription());

        Date newDate = new Date(System.currentTimeMillis());
        article.setPublishedDate(newDate);
        assertEquals(newDate, article.getPublishedDate());

        article.setContent("New Content");
        assertEquals("New Content", article.getContent());
    }

    @Test
    void testImageUrlGeneration() {
        article.setImageUrl("Custom URL");
        assertEquals("/com/example/newsapplicationversion1/images/1.jpg", article.getImageUrl());
    }

    @Test
    void testToString() {
        String expectedToString = "Article [articleId=1, source=TestSource, title=Test Title, " +
                "author=Test Author, category=Technology, description=Test Description, publishedDate=" + testDate + "]";
        assertEquals(expectedToString, article.toString());
    }
}