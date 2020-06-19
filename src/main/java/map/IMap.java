package map;

import TreantGenerator.iTreeMapGenerator.TreeTreantNode;

/**
 *
 The interface declares methods for working on the Map structure
 * @param <K> key
 * @param <V> value
 */
public interface IMap<K extends Comparable<? super K>, V> {

    /**
     * Method returns size of structure: amount of elements in Map
     * @return {@code int} amount of elements
     */
    int size();

    /**
     * Checks the structure for elements
     * @return {@code true/false}
     */
    boolean isEmpty();

    /**
     *
     Returns the value corresponding to the entered key
     * @param key key
     * @return {@code V} value
     */
    V get(K key);

    /**
     * Places a key-value pair in a structure
     * @param key key
     * @param value value
     * @return {@code V} value
     */
    V put(K key, V value);

    /**
     * Delete a key-value pair from structure
     * @param key
     * @return {@code V} value
     */
    V remove(K key);

    /**
     * Clear all map structure
     */
    void clear();

    /**
     * Translate structure tree in Treant Tree for Treant Generator
     * @return Treant Tree
     */
    TreeTreantNode toTreantNode();
}
