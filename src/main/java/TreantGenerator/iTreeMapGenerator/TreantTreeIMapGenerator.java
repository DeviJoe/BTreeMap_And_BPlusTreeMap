package TreantGenerator.iTreeMapGenerator;

import TreantGenerator.AbstractTreantGenerator;
import TreantGenerator.AbstractTreeTreantNode;
import map.IMap;

import java.io.File;
import java.util.List;

/**
 * Реализует класс для постройки кода структуры дерева.
 * Предназначен для обрабтки деревьев типа IMap
 * @see AbstractTreantGenerator
 */
public class TreantTreeIMapGenerator extends AbstractTreantGenerator {

    /**
     * Исходное дерево
     */
    IMap map;
    /**
     * Сгенерированное структурное дерево
     */
    TreeTreantNode head;

    public TreantTreeIMapGenerator(IMap map, String container, String treeType, String filePath) {
        super(container, treeType, filePath);
        this.map = map;

    }

    public TreantTreeIMapGenerator(IMap map, String container, String filePath) {
        super(container, filePath);
        this.map = map;
    }

    /**
     * Процесс конвертации возложен на сами структуры
     */
    private void convertIMapToTreantTree() {
        head = map.toTreantNode();
    }

    @Override
    protected String generateNodeView(String label) {
        return "innerHTML: \"" + label + "\",";
    }

    @Override
    protected String generateChildrenArray(List<AbstractTreeTreantNode> nodes) {
        {
            String code;


            code = "children: [";

            if (nodes != null) {
                for (int i = 0; i < nodes.size(); i++) {
                    code += "{ ";
                    code += generateNodeView(nodes.get(i).nodeView);
                    code += generateChildrenArray(nodes.get(i).nodes);
                    code += "},";
                }
            }
            code += "] ";

            return code;
        }
    }

    @Override
    public File generate() {
        convertIMapToTreantTree();
        generateHeader();
        code += generateNodeView(head.nodeView);
        code += generateChildrenArray(head.nodes);
        generateFooter();
        return createJSFile();
    }


}
