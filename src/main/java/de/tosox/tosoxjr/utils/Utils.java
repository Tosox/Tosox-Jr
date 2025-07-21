package de.tosox.tosoxjr.utils;

import java.util.concurrent.Callable;

public class Utils {
    public static <T> T getFromCallable(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        }
    }
}
