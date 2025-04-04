package src;
import java.sql.*;
import java.util.Arrays;

/**
 * QueryDB is a command-line Java program that connects to an SQLite database
 * and executes predefined SQL queries based on user input.
 * The program accepts a query number (1-6) as a command-line argument and, if
 * required,
 * additional parameters to execute specific queries.
 */
public class QueryDB {
    public static final String DB_PATH = "databases/database.db";

    /**
     * Main method that processes command-line arguments and executes the
     * corresponding SQL query.
     * 
     * @param args Command-line arguments: first argument should be an integer (1-6)
     *             indicating the query type.
     *             Additional arguments may be required depending on the query.
     * @throws SQLException if a database access error occurs.
     */
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            // ensure there are arguments
            if (args.length == 0) {
                throwArgsMessage();
                return;
            }

            // switch case for each integer in the first argument
            switch (args[0]) {
                case "1":
                    executeQuery("SELECT title FROM Movie;", connection);
                    break;
                case "2":
                    if (args.length < 2) {
                        throwArgsMessage(2, "'movie title'");
                        break;
                    }
                    String movieTitle = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    executeQuery(
                            "SELECT A.FirstName, A.LastName FROM Actor A JOIN Cast C ON A.ActorID = C.ActorID JOIN Movie M ON C.MovieID = M.MovieID WHERE M.title = '"
                                    + movieTitle + "';",
                            connection);
                    break;
                case "3":
                    if (args.length != 5) {
                        throwArgsMessage(3,
                                "'Actor FirstName', 'Actor LastName', 'Director FirstName', 'Director LastName'");
                        break;
                    }
                    executeQuery(
                            "SELECT M.plot FROM Movie M JOIN Cast C ON M.MovieID = C.MovieID JOIN Actor A ON C.ActorID = A.ActorID JOIN Director D ON M.DirectorID = D.DirectorID WHERE A.FirstName = '"
                                    + args[1] + "' AND A.LastName = '" + args[2] + "' AND D.FirstName = '" + args[3]
                                    + "' AND D.LastName = '" + args[4] + "';",
                            connection);
                    break;
                case "4":
                    if (args.length != 3) {
                        throwArgsMessage(4, "'Actor FirstName', 'Actor LastName'");
                        break;
                    }
                    executeQuery(
                            "SELECT D.FirstName, D.LastName FROM Director D JOIN Movie M ON D.DirectorID = M.DirectorID JOIN Cast C ON M.MovieID = C.MovieID JOIN Actor A ON C.ActorID = A.ActorID WHERE A.FirstName = '"
                                    + args[1] + "' AND A.LastName = '" + args[2] + "';",
                            connection);
                    break;
                case "5":
                    executeQuery(
                            "SELECT A.FirstName, A.LastName, M.title FROM Actor A JOIN ActorAward AA ON A.ActorID = AA.ActorID JOIN Cast C ON A.ActorID = C.ActorID JOIN Movie M ON C.MovieID = M.MovieID;",
                            connection);
                    break;
                case "6":
                    executeQuery(
                            "SELECT M.title, M.IMDBrating, D.FirstName AS DirectorFirstName, D.LastName AS DirectorLastName FROM Movie M INNER JOIN Director D ON M.DirectorID = D.DirectorID WHERE M.IMDBrating >= 8.0;",
                            connection);
                    break;
                default:
                    throwArgsMessage();
                    break;
            }
        }
    }

    /**
     * Prints a generic usage message when invalid or no arguments are provided.
     */
    public static void throwArgsMessage() {
        System.out.println("Usage: {integer 1-6} {optional params each in single quotes}");
    }

    /**
     * Prints a usage message specifying required parameters for a specific query.
     * 
     * @param queryNum The query number for which the message is being displayed.
     * @param message  Description of the required parameters.
     */
    public static void throwArgsMessage(int queryNum, String message) {
        System.out.println("Usage: {" + queryNum + "} {" + message + "}");
    }

    /**
     * Executes the given SQL query and prints the results.
     * 
     * @param query      The SQL query string to be executed.
     * @param connection The database connection object.
     * @return A ResultSet containing the results of the query, or null if an error
     *         occurs.
     * @throws SQLException if a database access error occurs.
     */
    public static ResultSet executeQuery(String query, Connection connection) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            printResultSet(rs);
        } catch (SQLException e) {
            System.err.println("Error executing query: " + e.getMessage());
        }

        return null;
    }

    /**
     * Prints the contents of a ResultSet to the console.
     * 
     * @param rs The ResultSet containing the data to be printed.
     * @throws SQLException if a database access error occurs.
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        int counter = 1;
        while (rs.next()) {
            System.out.print(counter + ": ");
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = rs.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
            }
            System.out.println();
            counter++;
        }
    }
}