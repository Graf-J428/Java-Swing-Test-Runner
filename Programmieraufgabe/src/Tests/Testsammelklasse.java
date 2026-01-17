package Tests;

import org.junit.Ignore;
import org.junit.Test;
import turban.utils.ErrorHandler;
import turban.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Testsammelklasse extends MyGuifiableObject {

    private List<Class<?>> lstTestKlassen = new ArrayList<>();

    public Map<String, FlexibleTreeNode<MyGuifiableObject>> getNodeMap() {
        return nodeMap;
    }

    public void setNodeMap(Map<String, FlexibleTreeNode<MyGuifiableObject>> nodeMap) {
        this.nodeMap = nodeMap;
    }

    private Map<String, FlexibleTreeNode<MyGuifiableObject>> nodeMap = new HashMap<>();

    public List<Class<?>> getLstTestKlassen() {
        return lstTestKlassen;
    }

    public void setLstTestKlassen(List<Class<?>> lstTestKlassen) {
        this.lstTestKlassen = lstTestKlassen;
    }


     public Testsammelklasse(){
         super("Testsammelklasse");
         lstTestKlassen.add(Testklasse01.class);
         lstTestKlassen.add(Testklasse02.class);
         lstTestKlassen.add(Testklasse03.class);

     }

    public void addToGui(FlexibleTreeNode<MyGuifiableObject> tnRoot) {
        for (Class<?> clazz : lstTestKlassen) {
            FlexibleTreeNode<MyGuifiableObject> child = new FlexibleTreeNode<>(new MyGuifiableObject(clazz.getSimpleName()));
            tnRoot.add(child);

            List<String> testMethodenListe = ReflectionUtils.getMethodNamesWithAnnotation(clazz, Test.class);
            for (String s : testMethodenListe) {
                FlexibleTreeNode<MyGuifiableObject> methodChild = new FlexibleTreeNode<>(new MyGuifiableObject(s));
                child.add(methodChild);
                String key = clazz.getName() + "#" + s;
                nodeMap.put(key, methodChild);
                System.out.println("Map PUT key: " + key);
            }

            List<String> ignoreMethodenListe = ReflectionUtils.getMethodNamesWithAnnotation(clazz, Ignore.class);
            for (String s : ignoreMethodenListe) {
                FlexibleTreeNode<MyGuifiableObject> methodChild = new FlexibleTreeNode<>(new MyGuifiableObject(s));
                child.add(methodChild);
                String key = clazz.getName() + "#" + s;
                nodeMap.put(key, methodChild);
                System.out.println("Map PUT key: " + key);
            }

            nodeMap.put(clazz.getName(), child);
        }
    }



}
