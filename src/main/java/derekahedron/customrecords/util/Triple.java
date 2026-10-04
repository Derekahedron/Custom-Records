package derekahedron.customrecords.util;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public record Triple<A, B, C>(A a, B b, C c) {

    public <T, U, V> Triple<T, U, V> map(
            Function<? super A, ? extends T> mapperA,
            Function<? super B, ? extends U> mapperB,
            Function<? super C, ? extends V> mapperC) {
        return new Triple<>(mapperA.apply(a), mapperB.apply(b), mapperC.apply(c));
    }

    public <T> Triple<T, B, C> setA(T t) {
        return new Triple<>(t, b, c);
    }

    public <U> Triple<A, U, C> setB(U u) {
        return new Triple<>(a, u, c);
    }

    public <V> Triple<A, B, V> setC(V v) {
        return new Triple<>(a, b, v);
    }

    public <T> Triple<T, B, C> mapA(
            Function<? super A, ? extends T> mapper) {
        return new Triple<>(mapper.apply(a), b, c);
    }

    public <U> Triple<A, U, C> mapB(
            Function<? super B, ? extends U> mapper) {
        return new Triple<>(a, mapper.apply(b), c);
    }

    public <V> Triple<A, B, V> mapC(
            Function<? super C, ? extends V> mapper) {
        return new Triple<>(a, b, mapper.apply(c));
    }

    public <T, U, V> Optional<Triple<T, U, V>> mapOptional(
            Function<? super A, ? extends Optional<T>> mapperA,
            Function<? super B, ? extends Optional<U>> mapperB,
            Function<? super C, ? extends Optional<V>> mapperC) {
        Optional<T> t = mapperA.apply(a);
        Optional<U> u = mapperB.apply(b);
        Optional<V> v = mapperC.apply(c);
        if (t.isPresent() && u.isPresent() && v.isPresent()) {
            return Optional.of(new Triple<>(t.get(), u.get(), v.get()));
        } else {
            return Optional.empty();
        }
    }

    public <T> Optional<Triple<T, B, C>> mapOptionalA(
            Function<? super A, ? extends Optional<T>> mapper) {
        return mapper.apply(a).map(t -> new Triple<>(t, b, c));
    }

    public <U> Optional<Triple<A, U, C>> mapOptionalB(
            Function<? super B, ? extends Optional<U>> mapper) {
        return mapper.apply(b).map(u -> new Triple<>(a, u, c));
    }

    public <V> Optional<Triple<A, B, V>> mapOptionalC(
            Function<? super C, ? extends Optional<V>> mapper) {
        return mapper.apply(c).map(v -> new Triple<>(a, b, v));
    }

    public static <A, B, C> FriendlyByteBuf.Reader<Triple<A, B, C>> reader(
            FriendlyByteBuf.Reader<A> readerA,
            FriendlyByteBuf.Reader<B> readerB,
            FriendlyByteBuf.Reader<C> readerC) {
        return buffer -> new Triple<>(
                readerA.apply(buffer),
                readerB.apply(buffer),
                readerC.apply(buffer));
    }

    public static <A, B, C> FriendlyByteBuf.Writer<Triple<A, B, C>> writer(
            FriendlyByteBuf.Writer<A> writerA,
            FriendlyByteBuf.Writer<B> writerB,
            FriendlyByteBuf.Writer<C> writerC) {
        return (buffer, triple) -> {
            writerA.accept(buffer, triple.a);
            writerB.accept(buffer, triple.b);
            writerC.accept(buffer, triple.c);
        };
    }
}
