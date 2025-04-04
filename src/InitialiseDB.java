package src;
import java.io.*;
import java.nio.file.*;
import java.sql.*;

/**
 * InitialiseDB is a Java program that creates and initializes an SQLite
 * database.
 * It deletes any existing database file, recreates it using SQL schema files,
 * and ensures that tables are successfully created.
 */
public class InitialiseDB {
    public static final String DB_PATH = "databases/database.db";
    public static final String SCHEMA_DIR = "tables";

    /**
     * Main method that initializes the database.
     * It deletes any existing database file, creates a new one, enables foreign key
     * constraints,
     * and executes SQL scripts to create tables.
     */
    public static void main(String[] args) {
        try {
            File dbFile = new File(DB_PATH);
            if (dbFile.exists()) {
                dbFile.delete();
            }

            try (Connection dbConnection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
                try (Statement stmt = dbConnection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }

                createDBTables(dbConnection, SCHEMA_DIR);

                if (checkTablesExist(dbConnection)) {
                    System.out.println("OK");
                } else {
                    System.err.println("Database initialization failed.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads and executes SQL commands from the given directory to create database
     * tables.
     * 
     * @param conn          The database connection.
     * @param directoryPath The directory containing SQL schema files.
     * @throws IOException  If an error occurs while reading the files.
     * @throws SQLException If an error occurs while executing SQL statements.
     */
    private static void createDBTables(Connection conn, String directoryPath) throws IOException, SQLException {
        File dir = new File(directoryPath);
        File[] sqlFiles = dir.listFiles();

        // for each file in the DDL directory
        for (File file : sqlFiles) {
            String sql = new String(Files.readAllBytes(file.toPath())).trim(); // read all contents of the file
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.execute();
            } catch (SQLException e) {
                System.err.println("Error executing " + e.getMessage());
            }
        }
    }

    /**
     * Verifies that all required tables have been successfully created in the
     * database.
     * 
     * @param conn The database connection.
     * @return true if all tables exist, false otherwise.
     * @throws SQLException If an error occurs during the database query.
     */
    private static boolean checkTablesExist(Connection conn) throws SQLException {
        String[] tables = { "Actor", "ActorAward", "Cast", "Director", "DirectorAward", "Movie", "MovieAward" };
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?"; // get all the table names

        // replace ? with current table name to check there is a result
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (String table : tables) {
                stmt.setString(1, table);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        System.err.println("Table missing: " + table);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}