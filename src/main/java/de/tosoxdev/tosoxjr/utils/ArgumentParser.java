package de.tosoxdev.tosoxjr.utils;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class ArgumentParser {
    public static String getString(OptionMapping arg, String fallback) {
        return arg != null ? arg.getAsString() : fallback;
    }

    public static boolean getBoolean(OptionMapping arg, boolean fallback) {
        return arg != null ? arg.getAsBoolean() : fallback;
    }
}
