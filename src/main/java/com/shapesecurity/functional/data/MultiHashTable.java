package com.shapesecurity.functional.data;


import com.shapesecurity.functional.F;
import com.shapesecurity.functional.F2;
import com.shapesecurity.functional.Pair;

import org.jetbrains.annotations.NotNull;

// Map from keys to multiple values.
// This class does not distinguish between "key is present, but associated with empty list" and "key is not present". If you need that, don't use this class.
public class MultiHashTable<K, V> { // TODO should be elsewhere... and better
    @NotNull
    private final HashTable<K, ImmutableList<V>> data;

    private MultiHashTable(@NotNull HashTable<K, ImmutableList<V>> data) {
        this.data = data;
    }

    @NotNull
    public static <K, V> MultiHashTable<K, V> empty() { // uses equality (.equals)
        return new MultiHashTable<>(HashTable.empty());
    }

    @NotNull
    public static <K, V> MultiHashTable<K, V> emptyP() { // uses identity (==)
        return new MultiHashTable<>(HashTable.emptyP());
    }

    @NotNull
    public MultiHashTable<K, V> put(@NotNull K key, @NotNull V value) {
        return new MultiHashTable<>(this.data.put(key, ImmutableList.cons(value, this.data.get(key).orJust(ImmutableList.nil()))));
    }

    @NotNull
    public MultiHashTable<K, V> remove(@NotNull K key) {
        return new MultiHashTable<>(this.data.remove(key));
    }

    @NotNull
    public ImmutableList<V> get(@NotNull K key) {
        return this.data.get(key).orJust(ImmutableList.nil());
    }

    @NotNull
    public MultiHashTable<K, V> merge(@NotNull MultiHashTable<K, V> tree) { // default merge strategy: append lists.
        return this.merge(tree, ImmutableList::append);
    }

    @NotNull
    public MultiHashTable<K, V> merge(@NotNull MultiHashTable<K, V> tree, @NotNull F2<ImmutableList<V>, ImmutableList<V>, ImmutableList<V>> merger) {
        return new MultiHashTable<>(this.data.merge(tree.data, merger));
    }

    @NotNull
    public ImmutableList<Pair<K, ImmutableList<V>>> entries() {
        return this.data.entries();
    }

    // version: key is irrelevant
    @NotNull
    public <B> HashTable<K, B> toHashTable(@NotNull F<ImmutableList<V>, B> conversion) {
        //return this.data.foldLeft((acc, p) -> acc.put(p.a, conversion.apply(p.b)), HashTable.empty(this.data.hasher));
        return this.toHashTable((k, vs) -> conversion.apply(vs));
    }

    // version: key is used
    @NotNull
    public <B> HashTable<K, B> toHashTable(@NotNull F2<K, ImmutableList<V>, B> conversion) {
        return this.data.foldLeft((acc, p) -> acc.put(p.a, conversion.apply(p.a, p.b)), HashTable.empty(this.data.hasher));
    }

    @NotNull
    public final ImmutableList<ImmutableList<V>> values() {
        return this.data.foldLeft((acc, p) -> acc.cons(p.b), ImmutableList.nil());
    }

    @NotNull
    public final ImmutableList<V> gatherValues() {
        return this.data.foldLeft((acc, p) -> acc.append(p.b), ImmutableList.nil());
    }

    @NotNull
    public final <B> MultiHashTable<K, B> mapValues(@NotNull F<V, B> f) {
        return new MultiHashTable<>(this.data.map(l -> l.map(f::apply)));
    }
}
