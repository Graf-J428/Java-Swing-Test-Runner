package Tests;

import turban.utils.ErrorHandler;
import turban.utils.IDebugable;
import turban.utils.IGuifiable;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

//ALle Kommentare waren bereits von Prof. Turban vorgegeben !!!

/**
 * Class for a more flexible TreeNode than DefaultMutableTreeNode. The class is
 * programmed as a generic working on IGuifiable objects
 *
 * @param <T> Type variable for the type of the user object.
 */
public class FlexibleTreeNode<T extends IGuifiable> extends DefaultMutableTreeNode implements IDebugable {


    public FlexibleTreeNode(T userObj) {
        super(userObj);
        // NOTE (BeTu; 2015-05-19): 'super'-Statement must always be first statement in
        // constructor.
        // Therefore the check for != null is performed afterwards. This is not so nice,
        // but it's quite ok.
        ErrorHandler.Assert(userObj != null, true, FlexibleTreeNode.class, "No user object provided!");
    }

    @Override
    public void setUserObject(Object userObj) {
        super.setUserObject(userObj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getUserObject() {
        return (T) super.getUserObject();
    }

    /**
     * Performs indentation (Einrückung) according to the level in the tree.
     */
    private void toDebugString_indent(StringBuilder sb, int indentLvl) {
        for (int i = 0; i < indentLvl; i++) {
            sb.append("  ");
        }
        if (indentLvl > 0) {
            sb.append("|-");
        }
    }

    @SuppressWarnings("unchecked")
    private void toDebugString_recursive(StringBuilder sb, int indentLvl, FlexibleTreeNode<T> tn) {
        toDebugString_indent(sb, indentLvl);
        sb.append(tn.toString());
        sb.append("\n");
        for (TreeNode tnChild : tn.getChildren()) {
            toDebugString_recursive(sb, indentLvl + 1, (FlexibleTreeNode<T>) tnChild);
        }
    }

    /**
     * String to be resolved for Debugging-Purposes
     *
     * @return Returns the content of the tree for debugging purposes.
     */
    public String toDebugString() {
        try {
            int capacity = countTreeNodes(this) * 70; // Initiale Cap. berechnen - bei größeren Bäumen wahrscheinlich effizienter!!
            StringBuilder sb = new StringBuilder(capacity);
            toDebugString_recursive(sb, 0, this);
            return sb.toString();
        } catch (Throwable th) {
            return "Unable to fully resolve " + this.getClass().getName() + " [" + this.toString() + "]";
        }
    }

    /**
     * Overwritten toString(). The method now calls toGuiString of the generic
     * object to do easier working with the IGuifiable-Pattern. As this method is
     * used by JTree.
     *
     * @return the string to be displayed in the GUI (e.g., in JTree)
     */
    @Override
    public String toString() {
        try {
            return ((IGuifiable) this.getUserObject()).toGuiString();
        } catch (Throwable th) {
            ErrorHandler.logException(th, false, FlexibleTreeNode.class, "Error resolving toGuiString()");
            return this.getClass().getName() + ": [Unresolvable Symbol!]";
        }
    }

    /**
     * Returns the children as Iterator
     *
     * NOTE: Hier als anonyme Implementierung realisiert -> wir nutzen children(),
     * das eine Enumeration zurückgibt.
     */
    public Iterable<FlexibleTreeNode<T>> getChildren() {
        @SuppressWarnings("rawtypes")
        final Enumeration enumChildren = this.children();
        return new Iterable<FlexibleTreeNode<T>>() {
            public Iterator<FlexibleTreeNode<T>> iterator() {
                return new Iterator<FlexibleTreeNode<T>>() {
                    public boolean hasNext() {
                        return enumChildren.hasMoreElements();
                    }

                    @SuppressWarnings("unchecked")
                    public FlexibleTreeNode<T> next() {
                        return (FlexibleTreeNode<T>) enumChildren.nextElement();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }


    public static int countTreeNodes(TreeNode tnStart) {
        if (tnStart == null) {
            return 0;
        }

        int iCount = 1; // 0+1 (==this node)
        for (int i = 0; i < tnStart.getChildCount(); i++) {
            TreeNode tnChild = tnStart.getChildAt(i);
            iCount += countTreeNodes(tnChild);
        }
        return iCount;
    }

    /**
     * Gets all tree nodes of the tree defined by tnStart (including tnStart!) as a
     * list
     * HINWEIS: Diese Methode könnte man eigentlich auch static machen, weil
     * das eigentliche Knotenobjekt gar nicht verwendet wird. Ich habe darauf
     * verzichtet, weil wegen des generischen-Typparameters hier sonst Probleme
     * auftreten, die Sie noch mehr verwirren würden. -> Sehen Sie hierzu aber die
     * danach auskommentierte Methode, die zeigt, wie man Sie als static-Methode
     * deklarieren müsste - hauptsächlich müsste man dann einen neuen Typparameter
     * einführen.
     *
     * @param tnStart IN: The tree node to start
     * @return list with the tree nodes.
     */
    public /* static */ List<FlexibleTreeNode<T>> getAllTreeNodesAsList(FlexibleTreeNode<T> tnStart) {

        // Hinweis: Da in der Signatur oben das T verlangt wird können wir keine
        // static-Methode daraus machen, weil wir dazu eine Objektinstanz benötigen.

        List<FlexibleTreeNode<T>> lstToReturn = new ArrayList<>(); // Ineffizient!!!!!

        lstToReturn.add(tnStart);

        for (int i = 0; i < tnStart.getChildCount(); i++) {
            @SuppressWarnings("unchecked") // da der Cast in der folgenden Zeile nicht vom Kompiler geprueft werden kann entsteht ein Compiler Warning -> wird hier unterdrueckt!
            FlexibleTreeNode<T> tnChild = (FlexibleTreeNode<T>) tnStart.getChildAt(i);

            List<FlexibleTreeNode<T>> lstFromChild = getAllTreeNodesAsList(tnChild);
            lstToReturn.addAll(lstFromChild);                       // Ineffizient!!!!!
        }

        return lstToReturn;
    }


    /*
     * Andere Möglichkeit: Als static-Methode mit eigener Typvariable: public static
     * <R extends IGuifiable> List<GUI.FlexibleTreeNode<R>>
     * getAllTreeNodesAsList(GUI.FlexibleTreeNode<R> tnStart) { //Hinweis: Hier wird der
     * Startknoten übergeben, deshalb können wir eine static-Methode daraus machen.
     * List<GUI.FlexibleTreeNode<R>> lstToReturn=new ArrayList<GUI.FlexibleTreeNode<R>> ();
     * if(tnStart==null){ return lstToReturn; //Return empty list }
     *
     * lstToReturn.add(tnStart); for(int i=0; i< tnStart.getChildCount(); i++){
     *
     * @SuppressWarnings("unchecked") GUI.FlexibleTreeNode<R> tnChild=
     * (GUI.FlexibleTreeNode<R>)tnStart.getChildAt(i);
     * lstToReturn.addAll(getAllTreeNodesAsList(tnChild)); } return lstToReturn; }
     */

    private void getAllTreeNodesAsListMoreEfficient_recursive(List<FlexibleTreeNode<T>> lstResults,
                                                              FlexibleTreeNode<T> tnCurrent) {

        lstResults.add(tnCurrent);

        for (int i = 0; i < tnCurrent.getChildCount(); i++) {
            @SuppressWarnings("unchecked")
            FlexibleTreeNode<T> tnChild = (FlexibleTreeNode<T>) tnCurrent.getChildAt(i);
            getAllTreeNodesAsListMoreEfficient_recursive(lstResults, tnChild);
        }
    }

    /**
     * Effizientere Variante von getAllTreeNodesAsList, die nur einmal eine Liste
     * erzeugt und mit this arbeitet.
     *
     * @return list with the tree nodes.
     */
    public List<FlexibleTreeNode<T>> getAllTreeNodesAsListMoreEfficient() {

        List<FlexibleTreeNode<T>> lstResults = new ArrayList<>(countTreeNodes(this) + 5);

        getAllTreeNodesAsListMoreEfficient_recursive(lstResults, this);

        return lstResults;
    }


    public /* static */ List<FlexibleTreeNode<T>> getAllSubTreeNodesAsList(FlexibleTreeNode<T> tnStart) {

        List<FlexibleTreeNode<T>> lstRet = new ArrayList<>();
        // Wenn Startknoten dabei sein soll:
        // lstRet.add(tnStart);

        for (int i = 0; i < tnStart.getChildCount(); i++) {
            FlexibleTreeNode<T> tnChild = (FlexibleTreeNode<T>) tnStart.getChildAt(i);
            lstRet.add(tnChild); // Hier soll er nicht dabei sein.
            List<FlexibleTreeNode<T>> lstCh = getAllSubTreeNodesAsList(tnChild);
            lstRet.addAll(lstCh);
        }

        return lstRet;
    }


    // **************** Beispiel mit static-Methode und Typvariable:
    // Verwendung:
    // GUI.FlexibleTreeNode<...> tn= ...
    // List<GUI.FlexibleTreeNode<...>> lst =
    // GUI.FlexibleTreeNode.getAllSubTreeNodesAsList(tn);

    private static <Z extends IGuifiable> void getAllTreeNodesAsListMoreEfficient_recursive(FlexibleTreeNode<Z> tnStart,
                                                                                            List<FlexibleTreeNode<Z>> lstToReturn) {
        lstToReturn.add(tnStart);
        for (int i = 0; i < tnStart.getChildCount(); i++) {
            @SuppressWarnings("unchecked")
            FlexibleTreeNode<Z> tnChild = (FlexibleTreeNode<Z>) tnStart.getChildAt(i);
            // lstToReturn.add(tnChild); Für not including
            getAllTreeNodesAsListMoreEfficient_recursive(tnChild, lstToReturn);
        }
    }


    public static <Z extends IGuifiable> List<FlexibleTreeNode<Z>> getAllTreeNodesAsListMoreEfficient_old(
            FlexibleTreeNode<Z> tnStart) {
        List<FlexibleTreeNode<Z>> lstToReturn = new ArrayList<FlexibleTreeNode<Z>>();
        if (tnStart == null) {
            return lstToReturn;
        }
        getAllTreeNodesAsListMoreEfficient_recursive(tnStart, lstToReturn);
        return lstToReturn;
    }

    public List<FlexibleTreeNode<T>> getAllTreeNodesAsListMoreEfficient_old() {
        return getAllTreeNodesAsListMoreEfficient_old(this);
    }

    // GUI.FlexibleTreeNode<...> tn= ...
    // tn.getAllTreeNodesAsListMoreEfficient();

    /*
     * @FunctionalInterface public interface Predicate<T>{ boolean test(T t); //...
     * (weitere Default-Methoden) }
     *
     * Verwendung getAllNodesWithCondition: GUI.FlexibleTreeNode<MyGuifObj> tnRoot=...;
     * List<GUI.FlexibleTreeNode<MyGuifObj>> lstFoundTnds=
     * tnRoot.getAllNodesWithCondition( new
     * Predicate<GUI.FlexibleTreeNode<MyGuifObj>>(){ public boolean
     * test(GUI.FlexibleTreeNode<MyGuifObj> tn){ return tn.isLeaf(); //return
     * tn.getChildCount()>5; //return tn.getUserObject() instanceof KlassenTypX;
     * //return tn.getUserObject().toGuiString().contains ("test"); } } );
     */

    /**
     * Gets all tree nodes of the tree (including the start tree node) meeting a
     * condition
     *
     * @param cond IN: The condition to test as interface
     *
     * @return the treenodes meeting the condition
     */
    public List<FlexibleTreeNode<T>> getAllNodesWithCondition(Predicate<FlexibleTreeNode<T>> cond) {
        // ToDo: Probieren Sie das mal selbst noch:
        throw new UnsupportedOperationException();
    }
    public List<FlexibleTreeNode<T>> getLeaves(){
        List <FlexibleTreeNode<T>> treeNodeList = new ArrayList<>();

        if (this == null){
            return null;
        }
        else if (this.isLeaf()){
            treeNodeList.add(this);
        }
        else{
            for(int i =0; i<this.getChildCount();i++){
                @SuppressWarnings("unchecked")
                FlexibleTreeNode<T> treeNode = (FlexibleTreeNode<T>) this.getChildAt(i);
                treeNodeList.addAll(getLeaves());
            }
        }

        return treeNodeList;
    }

    public List<T> getAllUserObjects( ) {
        List<T> listOfUserObjects = new ArrayList<>();
        collectUserObjects(listOfUserObjects);
        return listOfUserObjects;
    }

    private void collectUserObjects(List<T> resultList) {
        resultList.add(this.getUserObject());

        for (int i = 0; i < this.getChildCount(); i++) {
            @SuppressWarnings("unchecked")
            FlexibleTreeNode<T> child = (FlexibleTreeNode<T>) this.getChildAt(i);
           child. collectUserObjects(resultList);
        }
    }

    public void forEach(BiConsumer<FlexibleTreeNode<T>,T> biConsumer){
     biConsumer.accept(this, this.getUserObject());
     for ( int i=0; i<this.getChildCount();i++)
     {
         FlexibleTreeNode<T> child=(FlexibleTreeNode<T>)this.getChildAt(i);
        child.forEach(biConsumer);
     }
    }


}
