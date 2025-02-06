package foby.client.utils;

import java.util.function.Supplier;

public class Singleton<T> {
    private final Supplier<T> supplier;
    private T inst;

    public Singleton(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Singleton<T> create(Supplier<T> supplier) {
        return new Singleton<>(supplier);
    }

    public T get() {
        if (inst == null) {
            inst = supplier.get();
        }

        return inst;
    }
}