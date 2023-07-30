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
package com.avrsandbox.jector.core.command;

import com.avrsandbox.jector.core.work.TaskExecutorsManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents generic method arguments for an annotated method with
 * the annotation {@link ExecuteOn}.
 * 
 * @author pavl_g
 */
public class MethodArguments {

    /**
     * A map of generic objects to be passed to the bound method.
     */
    protected Map<String, Object> args;

    /**
     * For empty initialization.
     */
    public MethodArguments() {
        this.args = new ConcurrentHashMap<>();
    }

    /**
     * Instantiates a method args object that wraps a map of objects.
     * 
     * @param args a map of objects representing the method parameters
     */
    public MethodArguments(Map<String, Object> args) {
        this.args = args;
    }

    /**
     * Retrieves the map of arguments, use this method inside your
     * bound method (injected dependency) to fetch the arguments passed from the 
     * {@link TaskExecutorsManager} instance (the injector instance).
     * 
     * @return a map of objects representing the method parameters
     */
    public Map<String, Object> getArgs() {
        return args;
    }

    /**
     * Sets the map reference to a new instance.
     * 
     * @param args the new map object
     */
    public void setArgs(Map<String, Object> args) {
        this.args = args;
    }
}
