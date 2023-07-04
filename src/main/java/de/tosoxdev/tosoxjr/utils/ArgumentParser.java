package de.tosoxdev.tosoxjr.utils;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ArgumentParser {
    public static String getString(OptionMapping arg) {
        return arg != null ? arg.getAsString() : null;
    }

    public static String getStringForced(OptionMapping arg) throws IllegalArgumentException {
        String argRaw = getString(arg);
        if (argRaw == null) {
            throw new IllegalArgumentException("Option is optional");
        }
        return argRaw;
    }

    public static boolean getBoolean(OptionMapping arg, boolean fallback) {
        return arg != null ? arg.getAsBoolean() : fallback;
    }
}
