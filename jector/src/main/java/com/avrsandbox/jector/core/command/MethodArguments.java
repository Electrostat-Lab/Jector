package com.avrsandbox.jector.core.command;

/**
 * Represents generic method arguments for an annotated method with
 * the annotation {@link RunOn}.
 * 
 * @param <T> a class generic used to specify the type of the arguments 
 *            passed for a bound method, use {@link Object} to pass heterogenous types.
 * 
 * @author pavl_g
 */
public class MethodArguments<T> {

    /**
     * An array of generic objects to be passed to the bound method.
     */
    protected T[] args;

    /**
     * Instantiates a method args object that wraps an array of generified objects.
     * 
     * @param args an array of generified objects
     */
    public MethodArguments(T[] args) {
        this.args = args;
    }

    /**
     * Retrieves the generified arguments, use this method inside your 
     * bound method (injected dependency) to fetch the arguments passed from the 
     * {@link com.avrsandbox.jector.core.work.TaskBinder} instance (the injector instance).
     * 
     * @return an array of generified objects
     */
    public T[] getArgs() {
        return args;
    }

    /**
     * Sets the args generified object array.
     * 
     * @param args the new array object
     */
    public void setArgs(T[] args) {
        this.args = args;
    }
}
