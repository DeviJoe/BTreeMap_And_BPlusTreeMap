package map;

import TreantGenerator.iTreeMapGenerator.TreeTreantNode;

import java.util.*;

/**
 * Класс описывает Map структуру, основанную на B+ Tree
 * <a href = "https://ru.wikipedia.org/wiki/B%E2%81%BA-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE">Описание алгоритма на вики</a>
 * @see TreeMap
 * @see HashMap
 * @see Map
 *
 * @author Devijoe
 * @version 1.8
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public class BPlusTreeMap<K extends Comparable<? super K>, V> implements IMap {

    /**
     * Степень дерева
     */
    private transient int treeDegree;
    /**
     * Степень дерева по умолчанию
     */
    private transient static final int DEFAULT_TREE_DEGREE = 4;

    /**
     * Корень дерева
     */
    private Node root;

    /**
     * Количество элементов
     */
    private int size;

    /**
     * Конструктор по умолчанию
     */
    public BPlusTreeMap() {
        this(DEFAULT_TREE_DEGREE);
    }

    /**
     * Конструктор с параметром
     * @param treeDegree степень дерева
     */
    public BPlusTreeMap(int treeDegree) {
        if (treeDegree <= 2) {
            throw new IllegalArgumentException("НЕКОРРЕКТНАЯ СТЕПЕНЬ ДЕРЕВА: " + treeDegree);
        }
        this.treeDegree = treeDegree;
        root = new LeafNode();
    }

    /**
     * Абстрактный класс {@code Node}
     * декларирующий поведение для всех узлов структуры
     * дерева вида B+ tree.
     * Также объявляет общий для узлов элемент - массив ключей
     */
    private abstract class Node {
        List<K> keys;
        List<Node> children;
        /**
         * Возвращает количетсво ключей
         */
        int keyQuantity() {
            return keys.size();
        }

        /**
         * Производит получение значения узла по ключу
         * @param key ключ
         * @return значение
         */
        abstract V getValue(K key);

        /**
         * Производит удаление значения из узла по заданному ключу
         * @param key ключ
         */
        abstract void removeValue(K key);

        /**
         * Помещает пару ключ-значение в узел
         * @param key ключ
         * @param value значение
         */
        abstract void putValue(K key, V value);

        /**
         * Возвращает первый ключ в узле
         * @return ключ
         */
        abstract K getFirstLeafKey();

        /**
         * Слияние текущего узла с узлом на входе
         * @param node узел
         */
        abstract void merge(Node node);

        /**
         * Разлом узла
         * @return образованный узел
         */
        abstract Node split();

        /**
         * Сообщает о переполнении массива ключей (
         * @return true/false
         */
        abstract boolean isOverflow();

        /**
         * Проверяет на половинную заполненность массива
         * @return true/false
         */
        abstract boolean isUnderflow();
    }

    /**
     *  Внутренний узел, занимающийся хранением и обработкой массива с потомками
     */
    private class InternalNode extends Node {



        InternalNode() {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        @Override
        V getValue(K key) {
            return getChild(key).getValue(key);
        }

        @Override
        void removeValue(K key) {
            Node child = getChild(key);
            child.removeValue(key);
            if (child.isUnderflow()) {
                Node childLeftSibling = getChildLeftSibling(key);
                Node childRightSibling = getChildRightSibling(key);
                Node left = childLeftSibling != null ? childLeftSibling : child;
                Node right = childLeftSibling != null ? child : childRightSibling;
                left.merge(right);
                removeChild(right.getFirstLeafKey());
                if (left.isOverflow()) {
                    Node sibling = left.split();
                    putChild(sibling.getFirstLeafKey(), sibling);
                }
                if (root.keyQuantity() == 0)
                    root = left;
            }
        }

        @Override
        void putValue(K key, V value) {
            Node child = getChild(key);
            child.putValue(key, value);
            if (child.isOverflow()) {
                Node sibling = child.split();
                putChild(sibling.getFirstLeafKey(), sibling);
            }
            if (root.isOverflow()) {
                Node sibling = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(sibling.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(sibling);
                root = newRoot;
            }
        }

        @Override
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }

        @Override
        void merge(Node sibling) {
            InternalNode node = (InternalNode) sibling;
            keys.add(node.getFirstLeafKey());
            keys.addAll(node.keys);
            children.addAll(node.children);
        }

        @Override
        Node split() {
            int from = keyQuantity() / 2 + 1;
            int to = keyQuantity();
            InternalNode sibling = new InternalNode();
            sibling.keys.addAll(keys.subList(from, to));
            sibling.children.addAll(children.subList(from, to + 1));

            keys.subList(from - 1, to).clear();
            children.subList(from, to + 1).clear();

            return sibling;
        }

        @Override
        boolean isOverflow() {
            return children.size() > treeDegree;
        }

        @Override
        boolean isUnderflow() {
            return children.size() < (treeDegree + 1) / 2;
        }

        /**
         * Находит потомка узла по ключу
         * @param key ключ
         * @return искомый {@code Node} потомок
         */
        Node getChild(K key) {
            int index = Collections.binarySearch(keys, key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            return children.get(childIndex);
        }

        /**
         * Производит удаление потомка узла по ключу
         * @param key ключ
         */
        void removeChild(K key) {
            int index = Collections.binarySearch(keys, key);
            if (index >= 0) {
                keys.remove(index);
                children.remove(index + 1);
            }
        }

        /**
         * Размещает в узле пары ключ-значение
         * @param key ключ
         * @param child значение
         */
        void putChild(K key, Node child) {
            int index = Collections.binarySearch(keys, key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (index >= 0) {
                children.set(childIndex, child);
            } else {
                keys.add(childIndex, key);
                children.add(childIndex + 1, child);
            }
        }

        /**
         * Возвращает левый соседский узел
         * @param key ключ
         * @return
         *      искомый узел, если н существует, или {@code null}
         *      если не существует
         */
        Node getChildLeftSibling(K key) {
            int index = Collections.binarySearch(keys, key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (childIndex > 0)
                return children.get(childIndex - 1);

            return null;
        }

        /**
         * Возвращает правый соседский узел
         * @param key ключ
         * @return
         *      искомый узел, если н существует, или {@code null}
         *      если не существует
         */
        Node getChildRightSibling(K key) {
            int index = Collections.binarySearch(keys, key);
            int childIndex = index >= 0 ? index + 1 : -index - 1;
            if (childIndex < keyQuantity())
                return children.get(childIndex + 1);

            return null;
        }
    }

    /**
     * Узел дерева, занимающийся хранением и обработкой массива значений
     */
    private class LeafNode extends Node {

        List<V> values;
        LeafNode next;

        LeafNode() {
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        V getValue(K key) {
            int index = Collections.binarySearch(keys, key);
            return index >= 0 ? values.get(index) : null;
        }

        @Override
        void removeValue(K key) {
            int index = Collections.binarySearch(keys, key);
            if (index >= 0) {
                keys.remove(index);
                values.remove(index);
                size--;
            }
        }

        @Override
        void putValue(K key, V value) {
            int index = Collections.binarySearch(keys, key);
            int valueIndex = index >= 0 ? index : -index - 1;
            if (index >= 0) {
                values.set(valueIndex, value);
            } else {
                keys.add(valueIndex, key);
                values.add(valueIndex, value);
            }
            if (root.isOverflow()) {
                Node sibling = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(sibling.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(sibling);
                root = newRoot;
            }
        }

        @Override
        K getFirstLeafKey() {
            return keys.get(0);
        }

        @Override
        void merge(Node sibling) {
            LeafNode node = (LeafNode) sibling;
            keys.addAll(node.keys);
            values.addAll(node.values);
            next = node.next;
        }

        @Override
        Node split() {
            LeafNode node = new LeafNode();
            int from = (keyQuantity() + 1) / 2;
            int to = keyQuantity();
            node.keys.addAll(keys.subList(from, to));
            node.values.addAll(values.subList(from, to));

            keys.subList(from, to).clear();
            values.subList(from, to).clear();

            node.next = next;
            next = node;
            return node;
        }

        @Override
        boolean isOverflow() {
            return values.size() > treeDegree - 1;
        }

        @Override
        boolean isUnderflow() {
            return values.size() < treeDegree / 2;
        }
    }

    /**
     * Возвращает значение, которое соответствует заданному числу
     * @param key ключ
     * @return найденное значение
     */
    public V get(Comparable key) {
        return root.getValue((K)key);
    }

    /**
     * Помещает в {@code Map} пару ключ-значение
     * @param key ключ
     * @param value значение
     */
    public V put(Comparable key, Object value) {
        root.putValue((K)key, (V)value);
        size++;
        return (V) value;
    }

    /**
     * Производит удаление пары ключ-значение по заданному ключу
     * @param key ключ
     */
    public V remove(Comparable key) {
        V val = get((key));
        root.removeValue((K)key);
        return val;
    }

    /**
     * Производит удаление структуры
     */
    public void clear() {
        root = null;
    }

    /**
     * Возвращает количество элементов внутри структуры
     * @return число пар ключ-значение
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет структуру на пустоту
     * @return {@code true/false}
     */
    public boolean isEmpty() {
        return size == 0;
    }


    private TreeTreantNode _toTreantTree(Node pointer) {
        TreeTreantNode node = new TreeTreantNode();

        if (pointer.keys != null && pointer != null) {
            node.nodeView = keysToString(pointer.keys);
        }

        if (pointer != null && pointer.children != null) {
            for (int i = 0; i < pointer.children.size(); i++) {
                if (pointer.children.get(i) != null) {
                    node.nodes.add(_toTreantTree(pointer.children.get(i)));
                }
            }
        }
        if (pointer == null) return null;
        return node;
    }

    public TreeTreantNode toTreantNode() {
        return _toTreantTree(root);
    }

    private String keysToString(List<K> keys) {
        String s = new String();
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i) == null) continue;
            s += keys.get(i).toString() + " ==> " + get(keys.get(i)) + " <br> ";
        }
        return s;
    }
}