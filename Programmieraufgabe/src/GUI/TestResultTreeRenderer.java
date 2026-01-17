package GUI;

import Tests.MyGuifiableObject;
import turban.utils.ErrorHandler;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TestResultTreeRenderer extends DefaultTreeCellRenderer {
    private Icon iconPassed;
    private Icon iconFailed;
    private Icon iconIgnored;

    public TestResultTreeRenderer() {

        try{
            iconPassed = scaleIcon(new ImageIcon(getClass().getResource("/icons/Button_Icon_Green.svg.png")),16,16);
            iconFailed = scaleIcon(new ImageIcon(getClass().getResource("/icons/Button_Icon_Red.svg.png")),16,16);
            iconIgnored = scaleIcon(new ImageIcon(getClass().getResource("/icons/Button_Icon_Yellow.svg.png")),16,16);
        }catch(NullPointerException nullPointerException){
            ErrorHandler.logException(nullPointerException,true,TestResultTreeRenderer.class,"Das icon {[0]}, {[1]}, {[2]} konnte nicht geladen werden !",iconPassed,iconFailed,iconIgnored);
        }
    }

    /**
     * Überschriebene Methode zur Darstellung der Baumknoten.
     * <p>
     * Prüft, ob das UserObject ein {@link MyGuifiableObject} ist und passt basierend
     * auf dessen Status ("passed", "failed", "ignored") das Icon des Labels an.
     * </p>
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        JLabel label = (JLabel) super.getTreeCellRendererComponent(
                tree, value, selected, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObj = node.getUserObject();

        if (userObj instanceof MyGuifiableObject o) {
            label.setText(o.toGuiString());

            switch (o.getStatus()) {
                case "passed" -> label.setIcon(iconPassed);
                case "failed" -> label.setIcon(iconFailed);
                case "ignored" -> label.setIcon(iconIgnored);
                default -> label.setIcon(null);
            }

        }

        return label;
    }
    /**
     * Skaliert ein gegebenes ImageIcon auf die gewünschte Breite und Höhe.
     * Nutzt {@code Image.SCALE_SMOOTH} für eine qualitativ hochwertige Skalierung.
     *
     * @param icon Das ursprüngliche ImageIcon.
     * @param width Gewünschte Breite.
     * @param height Gewünschte Höhe.
     * @return Ein neues, skaliertes ImageIcon.
     */
    private Icon scaleIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
