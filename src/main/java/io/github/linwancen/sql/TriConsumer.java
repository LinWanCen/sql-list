package io.github.linwancen.sql;

public interface TriConsumer<K, V, S> {
    void accept(K k, V v, S s);
}
