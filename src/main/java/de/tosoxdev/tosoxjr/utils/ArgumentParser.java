package de.tosoxdev.tosoxjr.utils;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArgumentParser {
    @Nullable
    public static String getString(OptionMapping arg) {
        return arg != null ? arg.getAsString() : null;
    }

    @NotNull
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
