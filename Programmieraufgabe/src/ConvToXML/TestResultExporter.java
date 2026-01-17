package ConvToXML;

import Tests.FlexibleTreeNode;
import Tests.MyGuifiableObject;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

public class TestResultExporter {
    /**
     * Exportiert die Testergebnisse aus der Map in eine XML-Datei.
     * <p>
     * Iteriert über alle Knoten und schreibt Name, Status und Output in eine einfache XML-Struktur.
     * Verwendet {@link #characterEntity(String)}, um Sonderzeichen XML-konform zu maskieren.
     * </p>
     *
     * @param nodeMap Die Map mit den Testergebnissen (Key: Methodenname, Value: TreeNode).
     * @param file Die Zieldatei für den Export.
     * @throws Exception Wenn Schreibzugriffe fehlschlagen.
     */
    public static void exportToXml(Map<String, FlexibleTreeNode<MyGuifiableObject>> nodeMap, File file) throws Exception {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.println("<testResults>");

            for (Map.Entry<String, FlexibleTreeNode<MyGuifiableObject>> entry : nodeMap.entrySet()) {
                String key = entry.getKey();
                MyGuifiableObject obj = entry.getValue().getUserObject();

                writer.println("  <testcase>");
                writer.println("    <name>" + characterEntity(key) + "</name>");
                writer.println("    <status>" + characterEntity(obj.getStatus()) + "</status>");
                writer.println("    <output>" + characterEntity(obj.getOutput()) + "</output>");
                writer.println("  </testcase>");
            }

            writer.println("</testResults>");
        }
    }

    /**
     * Hilfsmethode zum Maskieren von XML-Sonderzeichen.
     * Ersetzt &, <, >, " und ' durch ihre entsprechenden Entity-Referenzen (z.B. &amp;),
     * um valides XML zu gewährleisten.
     *
     * @param input Der rohe String.
     * @return Der maskierte String oder ein leerer String bei null-Input.
     */
    private static String characterEntity(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
