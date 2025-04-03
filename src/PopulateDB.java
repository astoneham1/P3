package src;
import java.sql.*;
import java.io.*;
import java.util.Arrays;

/**
 * PopulateDB is a Java program that reads CSV files and inserts their contents
 * into an SQLite database. It maps each file to a predefined database table
 * and ensures the data is inserted correctly.
 */
public class PopulateDB {
    public static final String DB_PATH = "database.db";
    public static final String DATA_DIR = "data";

    /**
     * Main method that reads data files from the specified directory and
     * inserts their contents into corresponding database tables.
     * 
     * @throws SQLException If a database access error occurs.
     */
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            File dir = new File(DATA_DIR);
            File[] dataFiles = dir.listFiles();
            Arrays.sort(dataFiles);
            int currentFile = 0;

            // repeat for each of the csv files
            for (File file : dataFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;

                    // take each line and add it to the database
                    while ((line = br.readLine()) != null) {
                        String[] currentRow = line.split(",");
                        insertRow(connection, currentRow, currentFile);
                    }

                    System.out.println("Populated data for table " + dataFiles[currentFile]);
                    currentFile += 1;
                } catch (FileNotFoundException e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inserts a row of data into the appropriate database table.
     * The first column (primary key) is automatically skipped unless the table is
     * "Cast".
     * 
     * @param connection  The database connection.
     * @param elements    The data values to be inserted.
     * @param currentFile The index of the current file, mapping to a specific
     *                    table.
     * @throws SQLException If a database access error occurs.
     */
    public static void insertRow(Connection connection, String[] elements, int currentFile) throws SQLException {
        String[] tables = { "Actor", "ActorAward", "Cast", "Director", "DirectorAward", "Movie", "MovieAward" };
        String tableName = tables[currentFile];

        // query to retrieve column names, this allows us to skip over the first one
        String getColumnsQuery = "SELECT * FROM " + tableName + " LIMIT 1";

        try (PreparedStatement columnStmt = connection.prepareStatement(getColumnsQuery);
                ResultSet rs = columnStmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (columnCount <= 1) {
                throw new SQLException("Table " + tableName + " must have more than one column.");
            }

            // build column names and placeholders, handling "Cast" table differently as we
            // want to insert the first column
            StringBuilder columnNames = new StringBuilder();
            StringBuilder placeholders = new StringBuilder();

            // if table is not cast, start at second column
            if (!tableName.equals("Cast")) {
                for (int i = 2; i <= columnCount; i++) {
                    if (i > 2) {
                        columnNames.append(", ");
                        placeholders.append(", ");
                    }
                    columnNames.append(metaData.getColumnName(i));
                    placeholders.append("?");
                }
                // if table is cast, start at first column
            } else {
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        columnNames.append(", ");
                        placeholders.append(", ");
                    }
                    columnNames.append(metaData.getColumnName(i));
                    placeholders.append("?");
                }
            }

            // construct and execute the INSERT query
            String query = "INSERT INTO " + tableName + " (" + columnNames + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                for (int i = 0; i < elements.length; i++) {
                    pstmt.setString(i + 1, elements[i]);
                }
                pstmt.executeUpdate();
            }
        }
    }
}