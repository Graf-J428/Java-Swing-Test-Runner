package WriteInDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExportToDB {
    private static final String DB_URL = "jdbc:sqlite:teststats.db";


    /**
     * Speichert das Ergebnis eines Testlaufs in einer SQLite-Datenbank.
     * <p>
     * Erstellt die Tabelle 'test_statistics', falls diese noch nicht existiert.
     * Verwendet PreparedStatements zum sicheren Einfügen der Daten, um SQL-Injection zu verhindern
     * und Datumsformate korrekt zu handhaben.
     * </p>
     *
     * @param start Startzeitpunkt des Testlaufs.
     * @param end Endzeitpunkt des Testlaufs.
     * @param total Anzahl aller Tests.
     * @param passed Anzahl erfolgreicher Tests.
     * @param failed Anzahl fehlgeschlagener Tests.
     */
    public static void saveToDatabase(LocalDateTime start, LocalDateTime end, int total, int passed, int failed) {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS test_statistics (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                total_tests INTEGER NOT NULL,
                passed_tests INTEGER NOT NULL,
                failed_tests INTEGER NOT NULL
            );
        """;

        String insertSQL = """
            INSERT INTO test_statistics (start_time, end_time, total_tests, passed_tests, failed_tests)
            VALUES (?, ?, ?, ?, ?);
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()){

            stmt.execute(createTableSQL);

             try(PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                 DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                 pstmt.setString(1, start.format(fmt));
                 pstmt.setString(2, end.format(fmt));
                 pstmt.setInt(3, total);
                 pstmt.setInt(4, passed);
                 pstmt.setInt(5, failed);
                 pstmt.executeUpdate();
             }
            System.out.println(" Statistik erfolgreich gespeichert.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Liest alle Einträge aus der Datenbank und gibt sie auf der Konsole aus.
     */
    public static void printDatabaseContents() {
        String query = "SELECT id, start_time, end_time, total_tests, passed_tests, failed_tests FROM test_statistics;";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("== Test Statistics ==");
            while (rs.next()) {
                int id = rs.getInt("id");
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");
                int total = rs.getInt("total_tests");
                int passed = rs.getInt("passed_tests");
                int failed = rs.getInt("failed_tests");
                System.out.printf(
                        "ID=%d | Start=%s | Ende=%s | Total=%d | Passed=%d | Failed=%d%n",
                        id, start, end, total, passed, failed
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
