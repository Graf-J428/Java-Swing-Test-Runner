package GUI;

import Tests.FlexibleTreeNode;
import Tests.MyGuifiableObject;
import Tests.Testsammelklasse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import turban.utils.ErrorHandler;
import turban.utils.ReflectionUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buttons {
    private final Testsammelklasse testsammelklasse;
    private final Map<String, FlexibleTreeNode<MyGuifiableObject>> nodeMap;
    private final Map<String, String> testOutputs = new HashMap<>();
    private volatile boolean stopButtonPressed = false;
    private JUnitCore junit = new JUnitCore();

    private LocalDateTime startZeitpunkt;
    private LocalDateTime endZeitpunkt;
    private int totalTests;
    private int successfulTests;
    private int failedTests;

    public LocalDateTime getStartZeitpunkt() { return startZeitpunkt; }
    public LocalDateTime getEndZeitpunkt() { return endZeitpunkt; }
    public int getTotalTests() { return totalTests; }
    public int getSuccessfulTests() { return successfulTests; }
    public int getFailedTests() { return failedTests; }

    public Buttons(Testsammelklasse testsammelklasse) {
        this.testsammelklasse = testsammelklasse;
        this.nodeMap = testsammelklasse.getNodeMap();
    }
    public Map<String, FlexibleTreeNode<MyGuifiableObject>> getNodeMap() {
        return nodeMap;
    }
    public void setStopButtonPressed(boolean stopButtonPressed) {
        this.stopButtonPressed = stopButtonPressed;
    }

    public String getOutputFor(String key) {
        return testOutputs.getOrDefault(key, "(Keine Ausgabe für diesen Test)");
    }

    /**
     * Steuert den Hauptablauf des Testprozesses.
     * <p>
     * Diese Methode initialisiert die Zähler, iteriert über alle registrierten Testklassen
     * und ruft die Update-Methoden für Methoden- und Klassenstatus auf.
     * Sie überwacht zudem das "Stop"-Flag, um den Testlauf vorzeitig abzubrechen,
     * und fängt Exceptions während des Durchlaufs ab, um den UI-Thread nicht crashen zu lassen.
     * </p>
     *
     * @param progressBar Die Fortschrittsanzeige der GUI (wird am Ende ausgeblendet).
     * @param button Der Start-Button (wird am Ende wieder aktiviert).
     * @param treeModel Das TreeModel, um Änderungen direkt in der GUI sichtbar zu machen.
     */
    public void startButtonMethod(JProgressBar progressBar, JButton button, DefaultTreeModel treeModel) {
        startZeitpunkt = LocalDateTime.now();
        totalTests = 0;
        successfulTests = 0;
        failedTests = 0;
        try {
            for (Class<?> clazz : testsammelklasse.getLstTestKlassen()) {
                if (stopButtonPressed) break;

                updateMethodStatuses(clazz, treeModel);
                updateIgnoredStatuses(clazz, treeModel);
                updateClassStatus(clazz, treeModel);

            }
        } catch (Exception e) {
            ErrorHandler.logException(e, true, Buttons.class, "Fehler beim Durchlaufen der Testklassen!");
        } finally {
            endZeitpunkt = LocalDateTime.now();

            SwingUtilities.invokeLater(() -> {
                progressBar.setVisible(false);
                button.setEnabled(true);
            });
        }
    }

    /**
     * Führt die mit @Test annotierten Methoden einer Klasse aus und aktualisiert deren Status im Baum.
     * <p>
     * Besonderheit: Diese Methode fängt den {@code System.out} Stream ab, um Ausgaben,
     * die während des Tests auf der Konsole gemacht werden, in einen String umzuleiten.
     * Dieser Output wird zusammen mit Assert-Fehlermeldungen im GUI-Objekt gespeichert.
     * </p>
     *
     * @param clazz Die aktuell geprüfte Testklasse.
     * @param treeModel Das Model zur Benachrichtigung der GUI über Änderungen.
     */
    private void updateMethodStatuses(Class<?> clazz, DefaultTreeModel treeModel) {
        List<String> testMethoden = ReflectionUtils.getMethodNamesWithAnnotation(clazz, Test.class);
        for (String method : testMethoden) {
            if (stopButtonPressed) break;

            String key = clazz.getName() + "#" + method;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            PrintStream oldOut = System.out;
            System.setOut(ps);

            JUnitCore junit = new JUnitCore();
            Result result = junit.run(Request.method(clazz, method));

            System.out.flush();
            System.setOut(oldOut);
            String sysOut = baos.toString();
            StringBuilder fullOutput = new StringBuilder(sysOut);

            if (!result.wasSuccessful()) {
                for (Failure failure : result.getFailures()) {
                    if (failure.getDescription().getMethodName().equals(method)) {
                        String failureMessage = failure.getMessage();
                        fullOutput.append("\n Assertion-Fehler: ").append(failureMessage);
                    }
                }
            } else {
                fullOutput.append("\n Test erfolgreich");
            }

            testOutputs.put(key, fullOutput.toString());

            FlexibleTreeNode<MyGuifiableObject> node = nodeMap.get(key);
            if (node != null) {
                MyGuifiableObject obj = node.getUserObject();
                obj.setStatus(result.wasSuccessful() ? "passed" : "failed");
                obj.setOutput(fullOutput.toString());
                treeModel.nodeChanged(node);
            }

            totalTests++;
            if (result.wasSuccessful()) successfulTests++;
            else failedTests++;
        }
    }
    /**
     * Sucht nach Methoden mit der @Ignore Annotation und markiert diese im Baum als ignoriert.
     * Führt keine Tests aus, setzt aber den Status und den Output-Text entsprechend.
     *
     * @param clazz Die aktuell geprüfte Testklasse.
     * @param treeModel Das Model zur Benachrichtigung der GUI.
     */
    private void updateIgnoredStatuses(Class<?> clazz, DefaultTreeModel treeModel) {
        List<String> ignored = ReflectionUtils.getMethodNamesWithAnnotation(clazz, Ignore.class);
        for (String method : ignored) {
            String key = clazz.getName() + "#" + method;

            testOutputs.put(key, " Test ignoriert");

            FlexibleTreeNode<MyGuifiableObject> node = nodeMap.get(key);
            if (node != null) {
                MyGuifiableObject obj = node.getUserObject();
                obj.setStatus("ignored");
                obj.setOutput(" Test ignoriert");
                treeModel.nodeChanged(node);
            }
        }
    }
    /**
     * Aggregiert den Status einer gesamten Klasse basierend auf ihren Kind-Methoden.
     * <p>
     * Logik der Status-Priorität:
     * 1. Wenn mindestens eine Methode "failed" ist -> Klasse ist "failed".
     * 2. Wenn keine Fehler, aber ignorierte Methoden existieren -> Klasse ist "ignored".
     * 3. Ansonsten -> Klasse ist "passed".
     * </p>
     *
     * @param clazz Die Testklasse, deren Gesamtstatus berechnet wird.
     * @param treeModel Das Model zur Aktualisierung des Elternknotens.
     */
    private void updateClassStatus(Class<?> clazz, DefaultTreeModel treeModel) {
        FlexibleTreeNode<MyGuifiableObject> classNode = nodeMap.get(clazz.getName());
        if (classNode != null) {
            boolean hasFailure = false;
            boolean hasIgnore = false;
            for (int i = 0; i < classNode.getChildCount(); i++) {
                FlexibleTreeNode<MyGuifiableObject> methodNode = (FlexibleTreeNode<MyGuifiableObject>) classNode.getChildAt(i);
                String status = methodNode.getUserObject().getStatus();
                if ("failed".equals(status)) hasFailure = true;
                else if ("ignored".equals(status)) hasIgnore = true;
            }
            String classStatus;
            if (hasFailure) {
                classStatus = "failed";
            } else if (hasIgnore) {
                classStatus = "ignored";
            } else {
                classStatus = "passed";
            }

            classNode.getUserObject().setStatus(classStatus);
            treeModel.nodeChanged(classNode);
        }
    }
}

