package de.tosoxdev.tosoxjr;

import java.util.List;

public abstract class GenericCommandBase<E> {
    private final String name;
    private final String description;

    public GenericCommandBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void handle(E event, List<String> args);
}
