package dev.badbird.jdacommand.provider;

public interface Provider<T> {

    default Class<?>[] getExtraTypes() {
        return null;
    }
    default boolean matchWithInstanceOf() {
        return false;
    }
}
