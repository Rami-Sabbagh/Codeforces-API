package com.github.rami_sabbagh.codeforces.api.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Represents a Codeforces API object.
 */
public abstract class CFObject {

    /**
     * The GSON instance used for serializing in toString();
     */
    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * The GSON instance used for pretty serializing in toStringPretty();
     */
    private static final Gson gsonPretty = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

    /**
     * Format the object in JSON style with nulls included, for debugging purposes.
     *
     * @return The object formatted in JSON style.
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " = " + gson.toJson(this);
    }

    /**
     * Pretty formats the object in JSON style with nulls included, for debugging purposes.
     *
     * @return The object pretty-formatted in JSON style.
     */
    public String toStringPretty() {
        return this.getClass().getSimpleName() + " = " + gsonPretty.toJson(this);
    }
}
