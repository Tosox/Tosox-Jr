package de.tosoxdev.tosoxjr.utils;

import java.util.concurrent.Callable;

public class Utils {
    public static String getStringFromCallable(Callable<String> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            return null;
        }
    }
}
