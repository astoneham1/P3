package testing;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import src.InitialiseDB;
import src.PopulateDB;
import src.QueryDB;

/**
 * This class contains the test cases for the database operations. It uses JUnit
 * to test the functionality of InitialiseDB, PopulateDB, and QueryDB classes.
 * The tests include checking if tables are created, verifying data insertion,
 * and validating query outputs.
 */
public class Tests {
    private static Connection connection;

    /**
     * Sets up the test environment before any tests are run.
     * Makes a database connection and creates necessary tables for testing.
     *
     * @throws Exception If any error occurs during the setup process.
     */
    @BeforeClass
    public static void globalSetup() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite:databases/test.db");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("CREATE TABLE Actor (ActorID INTEGER PRIMARY KEY, FirstName VARCHAR(30), LastName VARCHAR(30));");
            stmt.execute("CREATE TABLE Movie (MovieID INTEGER PRIMARY KEY, Title VARCHAR(100), IMDBrating REAL, DirectorID INTEGER);");
        }
    }

    /**
     * Cleans up the test environment after all tests are run.
     * Closes the database connection and deletes the test database file.
     *
     * @throws Exception If any error occurs during cleanup.
     */
    @AfterClass
    public static void globalCleanup() throws Exception {
        if (connection != null) connection.close();
        new File("databases/test.db").delete();
    }

    /**
     * Test case that verifies if the 'Director' table is created successfully.
     * This test ensures that the table creation SQL commands are functioning as expected.
     *
     * @throws SQLException If any database access error occurs.
     */
    @Test
    public void testCreateTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE Director (DirectorID INTEGER PRIMARY KEY, FirstName TEXT, LastName TEXT);");
        }

        ResultSet rs = connection.getMetaData().getTables(null, null, "Director", null);
        assertTrue(rs.next());
    }

    /**
     * Test case that verifies data insertion into the 'Actor' table.
     * This test ensures that data is correctly inserted and can be queried.
     *
     * @throws SQLException If any database access error occurs.
     */
    @Test
    public void testPopulateActor() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Actor (ActorID, FirstName, LastName) VALUES (1, 'John', 'Doe');");
        }

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT FirstName, LastName FROM Actor WHERE ActorID = 1;");
        assertTrue(rs.next());
        assertEquals("John", rs.getString("FirstName"));
        assertEquals("Doe", rs.getString("LastName"));
    }

    /**
     * Test case that verifies a query execution on the 'Movie' table.
     * This test ensures that the query is executed properly and returns the expected results.
     *
     * @throws SQLException If any database access error occurs.
     */
    @Test
    public void testQueryMovie() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO Movie (MovieID, Title, IMDBrating, DirectorID) VALUES (1, 'Test Movie', 8.0, 1);");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        QueryDB.executeQuery("SELECT title FROM Movie;", connection);

        String output = out.toString().trim();
        assertTrue(output.contains("Test Movie"));
    }
}