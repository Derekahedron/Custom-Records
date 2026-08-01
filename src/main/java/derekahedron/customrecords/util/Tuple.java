package derekahedron.customrecords.util;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public record Tuple<A, B>(A a, B b) {

    public <T, U> Tuple<T, U> map(
            Function<? super A, ? extends T> mapperA,
            Function<? super B, ? extends U> mapperB) {
        return new Tuple<>(mapperA.apply(a), mapperB.apply(b));
    }

    public <T> Tuple<T, B> setA(T t) {
        return new Tuple<>(t, b);
    }

    public <U> Tuple<A, U> setB(U u) {
        return new Tuple<>(a, u);
    }

    public <T> Tuple<T, B> mapA(
            Function<? super A, ? extends T> mapper) {
        return new Tuple<>(mapper.apply(a), b);
    }

    public <U> Tuple<A, U> mapB(
            Function<? super B, ? extends U> mapper) {
        return new Tuple<>(a, mapper.apply(b));
    }

    public <T, U> Optional<Tuple<T, U>> mapOptional(
            Function<? super A, ? extends Optional<T>> mapperA,
            Function<? super B, ? extends Optional<U>> mapperB) {
        Optional<T> t = mapperA.apply(a);
        Optional<U> u = mapperB.apply(b);
        if (t.isPresent() && u.isPresent()) {
            return Optional.of(new Tuple<>(t.get(), u.get()));
        } else {
            return Optional.empty();
        }
    }

    public <T> Optional<Tuple<T, B>> mapOptionalA(
            Function<? super A, ? extends Optional<T>> mapper) {
        return mapper.apply(a).map(t -> new Tuple<>(t, b));
    }

    public <U> Optional<Tuple<A, U>> mapOptionalB(
            Function<? super B, ? extends Optional<U>> mapper) {
        return mapper.apply(b).map(u -> new Tuple<>(a, u));
    }

    public static <A, B> FriendlyByteBuf.Reader<Tuple<A, B>> reader(
            FriendlyByteBuf.Reader<A> readerA,
            FriendlyByteBuf.Reader<B> readerB) {
        return buffer -> new Tuple<>(
                readerA.apply(buffer),
                readerB.apply(buffer));
    }

    public static <A, B> FriendlyByteBuf.Writer<Tuple<A, B>> writer(
            FriendlyByteBuf.Writer<A> writerA,
            FriendlyByteBuf.Writer<B> writerB) {
        return (buffer, tuple) -> {
            writerA.accept(buffer, tuple.a);
            writerB.accept(buffer, tuple.b);
        };
    }
}
