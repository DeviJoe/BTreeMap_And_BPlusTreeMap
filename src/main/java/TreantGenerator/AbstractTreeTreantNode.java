package TreantGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс, декларирует общее описание ячейки для Treant дерева
 */
public abstract class AbstractTreeTreantNode {

    /**
     * В эту переменную пишется все описание узла
     */
    public String nodeView;
    /**
     * Сюда загружается лист с потомками узла, к которым идут связи
     */
    public List<AbstractTreeTreantNode> nodes;

    /**
     * Конструктор узла
     * @param nodeView - описание узла
     * @param nodes - лист потомков
     */
    public AbstractTreeTreantNode(String nodeView, List<AbstractTreeTreantNode> nodes) {
        this.nodeView = nodeView;
        this.nodes = nodes;
    }

    /**
     * Пустой конструктор
     */
    public AbstractTreeTreantNode() {
        nodes = new ArrayList<>();
    }

    public String getNodeView() {
        return nodeView;
    }

    public List<AbstractTreeTreantNode> getNodes() {
        return nodes;
    }
}
