package de.tosoxdev.tosoxjr.utils;

import java.util.List;

public class ArgumentParser {
    public static String get(List<String> args, int idx) {
        try {
            return args.get(idx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}
