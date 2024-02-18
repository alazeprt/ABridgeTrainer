package com.alazeprt.abt.utils;

import com.google.common.base.Objects;

public class Point<K, X, Y> {
    private K key;
    private X value1;
    private Y value2;
    public Point(K key, X value1, Y value2) {
        this.key = key;
        this.value1 = value1;
        this.value2 = value2;
    }

    public K getKey() {
        return key;
    }

    public X getValue1() {
        return value1;
    }

    public Y getValue2() {
        return value2;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public void setValue1(X value1) {
        this.value1 = value1;
    }

    public void setValue2(Y value2) {
        this.value2 = value2;
    }

    @Override
    public String toString() {
        return "Entry3{" +
                "key=" + key +
                ", value1=" + value1 +
                ", value2=" + value2 +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point<?, ?, ?> entry3 = (Point<?, ?, ?>) o;
        return Objects.equal(key, entry3.key) && Objects.equal(value1, entry3.value1) && Objects.equal(value2, entry3.value2);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, value1, value2);
    }
}
