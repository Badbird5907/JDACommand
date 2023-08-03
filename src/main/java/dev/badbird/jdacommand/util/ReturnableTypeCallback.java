package dev.badbird.jdacommand.util;

public interface ReturnableTypeCallback<R,A> {
    R call(A a);
}
