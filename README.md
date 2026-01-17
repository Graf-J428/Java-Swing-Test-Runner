# Java-Swing-Test-Runner
Ein grafisches Tool zur Ausführung und Visualisierung von JUnit-Tests mit Java Swing.

## Hauptfunktionen
* **Live-Visualisierung:** Dynamische Anzeige von Tests in einem `JTree` mit Status-Icons 
* **Dynamische Test-Erkennung:** Nutzung von Java Reflection, um Testklassen und `@Test`-Methoden zur Laufzeit zu finden und auszuführen.
* **Logging & Output:** Abfangen des `System.out`-Streams, um Konsolenausgaben der Tests direkt in der GUI anzuzeigen.
* **Export-Funktionen:**
  * **XML:** Exportieren der Testergebnisse für externe Weiterverarbeitung.
  * **SQLite Datenbank:** Speichern von Statistiken (Startzeit, Dauer, Erfolgsquote) in einer lokalen Datenbank (`teststats.db`).
* **Multithreading:** Auslagerung der Testausführung in separate Threads, damit die GUI (Swing Event Dispatch Thread) während langer Tests nicht einfriert.

## Technologien
* **Frontend:** Java Swing (JTree, Custom Renderers, Event Handling)
* **Backend:** Java Reflection API, JDBC (SQLite), IO Streams
* **Testing:** JUnit 4 Integration (`JUnitCore`)

## Architektur
* **`Buttons.java`:** Steuert die Testlogik und aktualisiert das Datenmodell.
* **`TreeGui.java`:** Hauptfenster und Layout der Anwendung.
* **`FlexibleTreeNode`:** Eine generische Erweiterung des Standard-TreeNodes für typsichere Datenhaltung.
* **`ExportToDB`:** Handhabt die JDBC-Verbindung zur Speicherung der Laufzeit-Statistiken.

## Voraussetzungen
Um das Projekt zu kompilieren, werden folgende Bibliotheken im Classpath benötigt:
* JUnit 4 library
* SQLite JDBC Driver
* SQLite-jdbc.jar
* turban.utils.jar
