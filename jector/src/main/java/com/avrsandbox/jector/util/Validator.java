package com.avrsandbox.jector.util;

import com.avrsandbox.jector.core.command.MethodArguments;
import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;

/**
 * Validates some rules against some parameters.
 * 
 * @author pavl_g
 */
public final class Validator {
    
    private Validator() {
    }

    public static void validateParametersLength(Method method, int length) {
        if (method.getParameters().length != length) {
            throw new IllegalArgumentException(method.getName() + "(...) parameters count must be " + length);
        }
    }

    public static void validateParameterType(Method method, int paramIndex, Class<?> validator) {
        /* Exiting without validation error if method is a non-parameterized method */
        if (method.getParameters().length < 1) {
            return;
        }
        if (!method.getParameters()[paramIndex].getType()
                        .isAssignableFrom(validator)) {
            throw new IllegalArgumentException(method.getName() + "(...) parameter[" + paramIndex +
                                                     "] is not of " + validator.getName() + " Type!");
        }
    }

    public static void validateNumberOfArgs(Parameter[] methodParams, Object[] inputParams) 
                            throws IllegalStateException, IllegalArgumentException {
        if (methodParams.length != inputParams.length) {
            throw new IllegalStateException("Number of input arguments doesn't match the required arguments!");
        }
    }
}
