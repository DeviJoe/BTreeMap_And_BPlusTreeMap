import TreantGenerator.iTreeMapGenerator.TreantTreeIMapGenerator;
import map.BPlusTreeMap;
import map.BTreeMap;
import map.IMap;

public class Main {

    public static void main(String[] args) {
        IMap<Integer, String> bPlusTreeMap = new BPlusTreeMap<>(4);

        bPlusTreeMap.put(0, "mother");
        bPlusTreeMap.put(1, "father");
        bPlusTreeMap.put(2, "grandpa");
        bPlusTreeMap.put(3, "grandma");
        bPlusTreeMap.put(4, "mother-in-law");
        bPlusTreeMap.put(5, "master");
        bPlusTreeMap.put(6, "slave");
        bPlusTreeMap.put(7, "sister");
        bPlusTreeMap.put(8, "brother");
        bPlusTreeMap.put(9, "son");
        bPlusTreeMap.put(10, "wife");
        bPlusTreeMap.put(11, "husband");
        bPlusTreeMap.put(12, "cousin");
        bPlusTreeMap.put(13, "ant");

        IMap<Integer, String> bTreeMap = new BTreeMap<>(2);

        bTreeMap.put(0, "mother");
        bTreeMap.put(1, "father");
        bTreeMap.put(2, "grandpa");
        bTreeMap.put(3, "grandma");
        bTreeMap.put(4, "mother-in-law");
        bTreeMap.put(5, "master");
        bTreeMap.put(6, "slave");
        bTreeMap.put(7, "sister");
        bTreeMap.put(8, "brother");
        bTreeMap.put(9, "son");
        bTreeMap.put(10, "wife");
        bTreeMap.put(11, "husband");
        bTreeMap.put(12, "cousin");
        bTreeMap.put(13, "aunt");
        bTreeMap.put(14, "uncle");
        bTreeMap.put(15, "father-in-law");

       TreantTreeIMapGenerator generator1 = new TreantTreeIMapGenerator(bPlusTreeMap, "bPlusTreeMap", "tree1", "bPlusTree.js");
        TreantTreeIMapGenerator generator2 = new TreantTreeIMapGenerator(bTreeMap, "bTreeMap", "tree2","bTree.js");
        generator1.generate();
        generator2.generate();
    }
}
