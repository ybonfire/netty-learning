package org.ybonfire.netty.common.model;

import jdk.nashorn.internal.objects.annotations.Getter;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:38
 */
public class Pair<K, V> {
    private final K key;
    private final V value;

    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
