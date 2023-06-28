package de.tosoxdev.tosoxjr;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericManagerBase<T, E> {
    private final List<T> elements = new ArrayList<>();

    public List<T> getElements() {
        return elements;
    }

    protected void addElement(T elem) throws IllegalArgumentException {
        boolean itemExists = elements.stream().anyMatch(i -> ((GenericCommandBase<?>) i).getName().equalsIgnoreCase(((GenericCommandBase<?>) elem).getName()));
        if (itemExists) {
            throw new IllegalArgumentException("Found a duplicate item in the list");
        }
        elements.add(elem);
    }

    @Nullable
    public T getElement(String name) {
        return elements.stream()
                .filter(cmd -> ((GenericCommandBase<?>) cmd).getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    protected abstract void handle(E event);
}
