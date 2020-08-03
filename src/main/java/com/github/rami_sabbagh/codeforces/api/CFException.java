package com.github.rami_sabbagh.codeforces.api;

/**
 * An exception caused by the CodeForces API.
 */
public class CFException extends Exception {

    /**
     * Create a new CodeForces API exception
     *
     * @param message The cause of the exception.
     */
    public CFException(String message) {
        super(message);
    }
}
