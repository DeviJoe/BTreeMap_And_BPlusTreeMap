package map;

import TreantGenerator.iTreeMapGenerator.TreeTreantNode;

import java.util.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Класс описывает Map структуру, основанную на B Tree
 * <a href = "https://ru.wikipedia.org/wiki/B-%D0%B4%D0%B5%D1%80%D0%B5%D0%B2%D0%BE">Описание алгоритма на вики</a>
 * @see TreeMap
 * @see HashMap
 * @see Map
 *
 * @author Devijoe
 * @version 1.8
 * @param <K> Тип ключа
 * @param <V> Тип значения
 */
public final class BTreeMap<K extends Comparable<? super K>, V> implements IMap {

    /* ПОЛЯ */
    /**
     * Степень дерева по умолчанию
     */
    private static final int DEFAULT_TREE_DEGREE = 2;

    /**
     * Степень дерева
     */
    private int treeDegree;

    /**
     * Карта для организации хранения пар ключ-значение
     */
    private final Map<K, V> map = new HashMap<>();

    private Node<K> root;

    /**
     * Класс, описывающий структуру узла дерева и реализующий функционал для работы с ним
     * @param <K> ключ
     */
    private class Node<K extends Comparable<? super K>> {

        /**
         * Кол-во пар "ключ-значение" в узле дерева
         */
        int size;

        /**
         * Массив ключей
         */
        K[] keys = (K[]) new Comparable[2 * treeDegree - 1];

        /**
         * Массив указателей на дочерние узлы
         */
        Node<K>[] children;

        /**
         * Класс, переводящий узел в разряд внутренних (создает массив потомков)
         */
        void makeInternal() {
            this.children = new Node[keys.length + 1];
        }

        /**
         * Проверка на то, является ли узел листом
         * @return true/false
         */
        boolean isLeaf() {
            return children == null;
        }

        /**
         * Разделение узла
         * @param i
         */
        private void split(int i) {
            Node<K> z = new Node<>();
            Node<K> y = children[i];

            if (!y.isLeaf()) {
                z.makeInternal();
            }

            z.size = treeDegree - 1;

            for (int j = 0; j < treeDegree - 1; ++j) {
                z.keys[j] = y.keys[j + treeDegree];
                y.keys[j + treeDegree] = null;
            }

            if (!y.isLeaf()) {
                for (int j = 0; j < treeDegree; ++j) {
                    z.children[j] = y.children[j + treeDegree];
                    y.children[j + treeDegree] = null;
                }
            }

            int oldSizeOfY = y.size;
            y.size = treeDegree - 1;
            K pushUpKey = y.keys[treeDegree - 1];

            for (int j = y.size; j < oldSizeOfY; ++j) {
                y.keys[j] = null;
            }

            for (int j = size; j >= i; --j) {
                children[j + 1] = children[j];
            }

            children[i + 1] = z;

            for (int j = size - 1; j >= i; --j) {
                keys[j + 1] = keys[j];
            }

            keys[i] = pushUpKey;
            size++;
        }

        private void removeFromLeaf(int removedKeyIndex) {
            for (int i = removedKeyIndex + 1; i < size; ++i) {
                keys[i - 1] = keys[i];
            }

            keys[--size] = null;
        }

        private <K extends Comparable<? super K>> int findKeyIndex(K key) {
            for (int i = 0; i != size; ++i) {
                if (keys[i].equals(key)) {
                    return i;
                }
            }

            return -1;
        }
    }

    /**
     * Пустой конструктор со значением по умолчанию
     */
    public BTreeMap() {
        treeDegree = DEFAULT_TREE_DEGREE;
        root = new Node<>();
    }

    /**
     * Конструктор дерева с пользовательской степенью дерева
     * @param treeDegree степень дерева
     */
    public BTreeMap(int treeDegree) {
        this.treeDegree = treeDegree;
        root = new Node<>();
    }

    /**
     * Метод размещает ключ в соответсвующем узле дерева
     * @param key ключ
     */
    private void bTreeInsertKey(K key) {
        Node<K> r = root;

        if (r.size == 2 * treeDegree - 1) {
            Node<K> node = new Node<>();
            root = node;
            node.makeInternal();
            node.children[0] = r;
            node.split(0);
            bTreeInsertNonFull(node, key);
        } else {
            bTreeInsertNonFull(r, key);
        }
    }


    private void bTreeInsertNonFull(Node<K> x, K k) {
        int i = x.size - 1;

        if (x.isLeaf()) {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                x.keys[i + 1] = x.keys[i];
                i--;
            }

            x.keys[i + 1] = k;
            x.size++;
        } else {
            while (i >= 0 && k.compareTo(x.keys[i]) < 0) {
                i--;
            }

            i++;

            if (x.children[i].size == 2 * treeDegree - 1) {
                x.split(i);

                if (k.compareTo(x.keys[i]) > 0) {
                    i++;
                }
            }

            bTreeInsertNonFull(x.children[i], k);
        }
    }

    /**
     * Метод возвращает кол-во пар "ключ-значение" в структуре
     * @return
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Метод проверки структуры на пустоту
     * @return true/false
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Метод возвращает значение по ключу
     * @param key ключ
     * @return значение
     */
    @Override
    public V get(Comparable key) {
        return map.get(key);
    }

    /**
     * Размещение в структуре дерева пары "ключ-значение"
     * @param key ключ
     * @param value значение
     * @return
     */
    @Override
    public V put(Comparable key, Object value) {

        if (map.containsKey(key)) {
            return map.put((K)key, (V)value);
        }

        bTreeInsertKey((K)key);
        map.put((K)key, (V)value);
        return null;
    }

    /**
     * Удаление из структуры пары "ключ-значение"
     * @param key ключ
     * @return удаленное значение
     */
    @Override
    public V remove(Comparable key) {
        if (map.containsKey( key)) {
            bTreeDeleteKey(root, (K) key);
            return map.remove(key);
        }

        return null;
    }

    @Override
    public void clear() {
        root = null;
    }

    private  <K extends Comparable<? super K>> Node<K> getMinimumNode(Node<K> x) {
        while (!x.isLeaf()) {
            x = x.children[0];
        }

        return x;
    }

    private  <K extends Comparable<? super K>> Node<K> getMaximumNode(Node<K> x) {
        while (!x.isLeaf()) {
            x = x.children[x.size];
        }

        return  x;
    }

    private void bTreeDeleteKey(Node<K> node, K key) {
        int keyIndex = node.findKeyIndex(key);

        if (keyIndex >= 0) {
            if (node.isLeaf()) {
                node.removeFromLeaf(keyIndex);
                return;
            }

            Node<K> leftChildren = node.children[keyIndex];

            if (leftChildren.size >= treeDegree) {
                Node<K> tmp = getMaximumNode(leftChildren);
                K keyPrime = tmp.keys[tmp.size - 1];
                bTreeDeleteKey(leftChildren, keyPrime);
                node.keys[keyIndex] = keyPrime;
                return;
            }

            Node<K> rightChildren = node.children[keyIndex + 1];

            if (rightChildren.size >= treeDegree) {
                Node<K> tmp = getMinimumNode(rightChildren);
                K keyPrime = tmp.keys[0];
                bTreeDeleteKey(rightChildren, keyPrime);
                node.keys[keyIndex] = keyPrime;
                return;
            }

            leftChildren.keys[leftChildren.size] = key;

            for (int i = 0, j = leftChildren.size + 1; i != rightChildren.size; ++i, ++j) {
                leftChildren.keys[j] = rightChildren.keys[i];
            }

            if (!leftChildren.isLeaf()) {
                for (int i = 0, j = leftChildren.size + 1; i != rightChildren.size + 1; ++i, ++j) {
                    leftChildren.children[j] = rightChildren.children[i];
                }
            }

            leftChildren.size = 2 * treeDegree - 1;

            if (!leftChildren.isLeaf()) {
                leftChildren.children[leftChildren.size] = rightChildren.children[rightChildren.size];
            }

            for (int i = keyIndex + 1; i < node.size; ++i) {
                node.keys[i - 1] = node.keys[i];
                node.children[i] = node.children[i + 1];
            }

            node.children[node.size] = null;
            node.keys[--node.size] = null;
            bTreeDeleteKey(leftChildren, key);

            if (node.size == 0) {
                root = leftChildren;
            }
        } else {
            int childIndex = -1;

            for (int i = 0; i < node.size; ++i) {
                if (key.compareTo(node.keys[i]) < 0) {
                    childIndex = i;
                    break;
                }
            }

            if (childIndex == -1) {
                childIndex = node.size;
            }

            Node<K> targetChild = node.children[childIndex];

            if (targetChild.size == treeDegree - 1) {
                if (childIndex > 0
                        && node.children[childIndex - 1].size >= treeDegree) {
                    if (targetChild.isLeaf()) {
                        Node<K> leftSibling = node.children[childIndex - 1];

                        K lastLeftSiblingKey =
                                leftSibling.keys[leftSibling.size - 1];

                        K keyToPushDown = node.keys[childIndex - 1];
                        node.keys[childIndex - 1] = lastLeftSiblingKey;

                        for (int i = targetChild.size - 1; i >= 0; --i) {
                            targetChild.keys[i + 1] = targetChild.keys[i];
                        }

                        targetChild.size++;
                        targetChild.keys[0] = keyToPushDown;
                        leftSibling.keys[--leftSibling.size] = null;
                    } else {
                        Node<K> leftSibling = node.children[childIndex - 1];

                        K lastLeftSiblingKey =
                                leftSibling.keys[leftSibling.size - 1];

                        Node<K> lastLeftSiblingChild =
                                leftSibling.children[leftSibling.size];

                        K keyToPushDown = node.keys[childIndex - 1];
                        node.keys[childIndex - 1] = lastLeftSiblingKey;

                        targetChild.children[targetChild.size + 1] =
                                targetChild.children[targetChild.size];

                        for (int i = targetChild.size - 1; i >= 0; --i) {
                            targetChild.keys[i + 1] = targetChild.keys[i];
                            targetChild.children[i + 1] =
                                    targetChild.children[i];
                        }

                        targetChild.size++;
                        targetChild.keys[0] = keyToPushDown;
                        targetChild.children[0] = lastLeftSiblingChild;
                        leftSibling.children[leftSibling.size] = null;
                        leftSibling.keys[--leftSibling.size] = null;
                    }
                } else if (childIndex < node.size
                        && node.children[childIndex + 1].size >= treeDegree) {
                    if (targetChild.isLeaf()) {
                        Node<K> rightSibling = node.children[childIndex + 1];

                        K firstRightSiblingKey = rightSibling.keys[0];

                        K keyToPushDown = node.keys[childIndex];
                        node.keys[childIndex] = firstRightSiblingKey;

                        for (int i = 1; i < rightSibling.size; ++i) {
                            rightSibling.keys[i - 1] = rightSibling.keys[i];
                        }

                        rightSibling.keys[--rightSibling.size] = null;

                        targetChild.keys[targetChild.size] = keyToPushDown;
                        targetChild.size++;
                    } else {
                        Node<K> rightSibling = node.children[childIndex + 1];

                        K firstRightSiblingKey = rightSibling.keys[0];
                        Node<K> firstRightSiblingChild =
                                rightSibling.children[0];

                        K keyToPushDown = node.keys[childIndex];
                        node.keys[childIndex] = firstRightSiblingKey;

                        for (int i = 1; i < rightSibling.size; ++i) {
                            rightSibling.keys[i - 1] = rightSibling.keys[i];
                            rightSibling.children[i - 1] =
                                    rightSibling.children[i];
                        }

                        rightSibling.children[rightSibling.size - 1] =
                                rightSibling.children[rightSibling.size];
                        rightSibling.children[rightSibling.size] = null;
                        rightSibling.keys[--rightSibling.size] = null;

                        targetChild.keys[targetChild.size] = keyToPushDown;
                        targetChild.children[++targetChild.size] =
                                firstRightSiblingChild;
                    }
                } else if (childIndex > 0) {
                    Node<K> leftSibling  = node.children[childIndex - 1];
                    if (targetChild.isLeaf()) {
                        K keyToPushDown = node.keys[childIndex - 1];
                        leftSibling.keys[leftSibling.size] = keyToPushDown;

                        for (int i = 0, j = leftSibling.size + 1;
                             i != targetChild.size; ++i, ++j) {
                            leftSibling.keys[j] = targetChild.keys[i];
                        }

                        leftSibling.size = 2 * treeDegree - 1;

                        for (int i = childIndex; i < node.size; ++i) {
                            node.keys[i - 1] = node.keys[i];
                            node.children[i] = node.children[i + 1];
                        }

                        node.keys[node.size - 1] = null;
                        node.children[node.size] = null;
                        node.size--;

                        if (node.size == 0) {
                            root = leftSibling;
                        }

                        targetChild = leftSibling;
                    } else {
                        K keyToPushDown = node.keys[childIndex - 1];
                        leftSibling.keys[leftSibling.size] = keyToPushDown;

                        for (int i = 0, j = leftSibling.size + 1;
                             i != targetChild.size; ++i, ++j) {
                            leftSibling.keys[j] = targetChild.keys[i];
                            leftSibling.children[j] =
                                    targetChild.children[i];
                        }

                        leftSibling.size = 2 * treeDegree - 1;
                        leftSibling.children[leftSibling.size] =
                                targetChild.children[targetChild.size];

                        for (int i = childIndex; i < node.size; ++i) {
                            node.keys[i - 1] = node.keys[i];
                            node.children[i] = node.children[i + 1];
                        }

                        node.keys[node.size - 1] = null;
                        node.children[node.size--] = null;

                        if (node.size == 0) {
                            root = leftSibling;
                        }

                        targetChild = leftSibling;
                    }
                } else {
                    Node<K> rightSibling = node.children[childIndex + 1];

                    if (targetChild.isLeaf()) {
                        K keyToPushDown = node.keys[childIndex];
                        targetChild.keys[targetChild.size] = keyToPushDown;

                        for (int i = 0, j = targetChild.size + 1;
                             i != rightSibling.size;
                             ++i, ++j) {
                            targetChild.keys[j] = rightSibling.keys[i];
                        }

                        targetChild.size = 2 * treeDegree - 1;

                        for (int i = childIndex + 1; i < node.size; ++i) {
                            node.keys[i - 1] = node.keys[i];
                            node.children[i] = node.children[i + 1];
                        }

                        node.children[node.size] = null;
                        node.keys[--node.size] = null;

                        if (node.size == 0) {
                            root = targetChild;
                        }
                    } else {
                        K keyToPushDown = node.keys[childIndex];
                        targetChild.keys[targetChild.size] = keyToPushDown;

                        for (int i = 0, j = targetChild.size + 1;
                             i != rightSibling.size;
                             ++i, ++j) {
                            targetChild.keys[j] = rightSibling.keys[i];
                            targetChild.children[j] = rightSibling.children[i];
                        }

                        targetChild.size = 2 * treeDegree - 1;
                        targetChild.children[targetChild.size] =
                                rightSibling.children[rightSibling.size];

                        for (int i = childIndex + 1; i < node.size; ++i) {
                            node.keys[i - 1] = node.keys[i];
                            node.children[i] = node.children[i + 1];
                        }

                        node.children[node.size - 1] = node.children[node.size];
                        node.children[node.size] = null;
                        node.keys[--node.size] = null;

                        if (node.size == 0) {
                            root = targetChild;
                        }
                    }
                }
            }

            bTreeDeleteKey(targetChild, key);
        }
    }

    private TreeTreantNode _toTreantTree(Node<K> pointer) {
        TreeTreantNode node = new TreeTreantNode();

        if (pointer.keys != null && pointer != null) {
            node.nodeView = keysToString(pointer.keys);
        }

        if (pointer.children != null && pointer != null) {
            for (int i = 0; i < pointer.children.length; i++) {
                if (pointer.children[i] != null) {
                    node.nodes.add(_toTreantTree(pointer.children[i]));
                }
            }
        }
        if (pointer == null) return null;
        return node;
    }

    public TreeTreantNode toTreantNode() {
        return _toTreantTree(root);
    }

    private String keysToString(K[] keys) {
        String s = new String();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null) continue;
            s += keys[i].toString() + " ==> " + get(keys[i]) + " <br> ";
        }
        return s;
    }

}
