package GUI;

import Tests.*;
import ConvToXML.TestResultExporter;
import turban.utils.ErrorHandler;

import WriteInDB.ExportToDB;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class TreeGui extends JFrame {
    private Testsammelklasse testsammelklasse;
    private JTree tree;
    private DefaultTreeModel treeModel;

    private FlexibleTreeNode<MyGuifiableObject> tnRoot;
    private  final JButton startButton;
    private final JButton stopButton;
    private Buttons button ;
    private JButton exportButton;
    private JButton exportStatsButton;


    public TreeGui() {
        setTitle("Tree GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        //Zentrierung
        setLocationRelativeTo(null);



        testsammelklasse = new Testsammelklasse();
        tnRoot = new FlexibleTreeNode<>(testsammelklasse);

        treeModel = new DefaultTreeModel(tnRoot);
        treeModel.setRoot(tnRoot);
        testsammelklasse= new Testsammelklasse();
        testsammelklasse.addToGui(tnRoot);
        button = new Buttons(testsammelklasse);

        tree = new JTree(treeModel);
        tree.setCellRenderer(new TestResultTreeRenderer());



        JScrollPane treeScrollPane = new JScrollPane(tree);

        JTextArea textAreaRight = new JTextArea();
        JScrollPane scrollRight = new JScrollPane(textAreaRight);

        JPanel mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, scrollRight);
        splitPane.setResizeWeight(0.3);
        splitPane.setContinuousLayout(true);

        mainPanel.add(splitPane, BorderLayout.CENTER);


        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(controlPanel, BorderLayout.SOUTH);


        JPanel leftButtons = new JPanel();
         startButton = new JButton("Start");
         stopButton = new JButton("Stop");
         exportButton = new JButton("Export XML");
         exportStatsButton = new JButton("Testlauf speichern");

        leftButtons.add(exportButton);
        leftButtons.add(startButton);
        leftButtons.add(stopButton);
        leftButtons.add(exportStatsButton);
        exportStatsButton.setEnabled(false);

        controlPanel.add(leftButtons, BorderLayout.WEST);



        JPanel rightControls = new JPanel(new GridLayout(2, 1, 2, 2));

        JCheckBox showRootBox = new JCheckBox("Show Root");

        rightControls.add(showRootBox);
        controlPanel.add(rightControls, BorderLayout.EAST);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );

        JProgressBar jProgressBar= new JProgressBar();
        jProgressBar.setIndeterminate(true);

        tree.setRootVisible(false);
         showRootBox.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 tree.setRootVisible(showRootBox.isSelected());
             }
         });



        tree.addTreeSelectionListener(e -> {
            Object selected = tree.getLastSelectedPathComponent();
            if (selected instanceof FlexibleTreeNode<?> node) {
                Object userObj = node.getUserObject();
                if (userObj instanceof MyGuifiableObject guifiable) {
                    String output=guifiable.getOutput();
                    textAreaRight.setText(output != null ? output : "(Keine Ausgabe vorhanden)");
                }
            }
        });

            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.setStopButtonPressed(false);
                    startButton.setEnabled(false);
                    jProgressBar.setVisible(true);
                    new Thread(()->button.startButtonMethod(jProgressBar,startButton,treeModel)).start();
                    exportStatsButton.setEnabled(true);
                }
            });
            stopButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    button.setStopButtonPressed(true);
                    jProgressBar.setVisible(false);
                }
            });

        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    TestResultExporter.exportToXml(button.getNodeMap(), selectedFile);
                } catch (Exception ex) {
                    ErrorHandler.logException(ex, true, getClass(), "Fehler beim Exportieren der Ergebnisse");
                }
            }
        });

        exportStatsButton.addActionListener(e -> {
            ExportToDB.saveToDatabase(
                    button.getStartZeitpunkt(),
                    button.getEndZeitpunkt(),
                    button.getTotalTests(),
                    button.getSuccessfulTests(),
                    button.getFailedTests()
            );
            exportStatsButton.setEnabled(false);
            ExportToDB.printDatabaseContents();

        });

    }



    public static void main(String[] args) {
        TreeGui treeGui =new TreeGui();
        treeGui.setVisible(true);
    }

}