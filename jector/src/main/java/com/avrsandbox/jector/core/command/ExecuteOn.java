package com.avrsandbox.jector.core.command;

import com.avrsandbox.jector.core.work.TaskReceiver;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

/**
 * Determines the directionality of execution of some methods inside an {@link TaskReceiver}.
 *
 * @author pavl_g
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecuteOn {
    
    /**
     * Specifies which threads will bind the annotated method, methods 
     * are bounded to their threads by a {@link TaskBinder} instance.
     * 
     * <p>
     * 
     * Threads specified must be registered using {@link TaskBinder#re}
     * before being able to receive annotated methods.
     * 
     * @return an array of classes representing the registered threads
     */
    Class<? extends TaskReceiver>[] receivers();
}
