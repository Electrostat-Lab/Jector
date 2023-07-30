/* 
* BSD 3-Clause License
*
* Copyright (c) 2023, The AvrSandbox Project, Jector Framework
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this
*    list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice,
*    this list of conditions and the following disclaimer in the documentation
*    and/or other materials provided with the distribution.
*
* 3. Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.avrsandbox.jector.util;

import java.lang.reflect.Parameter;
import java.lang.reflect.Method;
import java.lang.IllegalArgumentException;

/**
 * Validates some rules against some parameters.
 * 
 * @author pavl_g
 */
public final class Validator {
    
    /**
     * Private-access to inhibit instantiation.
     */
    private Validator() {
    }

    /**
     * Validates a method's parameters length against a particular number.
     * 
     * @param method the method to validate its parameters
     * @param length the number to validate against
     * @throws IllegalArgumentException if the validation fails to meet the criterion
     */
    public static void validateParametersLength(Method method, int length) throws IllegalArgumentException {
        if (method.getParameters().length != length) {
            throw new IllegalArgumentException(method.getName() + "(...) parameters count must be " + length);
        }
    }

    /**
     * Validates a method's parameter type against a particular type, namely 'validator' exiting
     * without a validation error if this method is a non-parameterized.
     * 
     * @param method the method to validate its parameter type
     * @param paramIndex the index of the method parameter to be validated
     * @param validator the type to validate against
     * @throws IllegalArgumentException if the indexed method parameter is not of class-type 'validator'
     */
    public static void validateParameterType(Method method, int paramIndex, Class<?> validator) throws IllegalArgumentException {
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

    /**
     * Validates the number of the arguments of a parameterized methods.
     * 
     * @param methodParams the method parameters
     * @param inputParams the user-input parameters to validate against
     * @throws IllegalArgumentException if the count of input parameters does not match the count of the method parameters
     */
    public static void validateNumberOfArgs(Parameter[] methodParams, Object[] inputParams) 
                                                          throws IllegalArgumentException {
        if (methodParams.length != inputParams.length) {
            throw new IllegalArgumentException("Number of input arguments doesn't match the required arguments!");
        }
    }
}
