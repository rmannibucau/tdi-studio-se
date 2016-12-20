package org.talend.codegen.flowvariables.runtime;

import org.talend.components.api.component.runtime.WriterWithFeedback;

/**
 * Processes data retrieved from wrapped component runtime classes (e.g. {@link Reader}, {@link WriterWithFeedback})
 */
abstract class DataProcessor {

    /**
     * Processes single data object
     * 
     * @param data data object retrieved from component runtime class
     * @return data object after processing
     */
    abstract Object processData(Object data);

    /**
     * Processes {@link Iterable} of data objects
     * 
     * @param dataIterable data {@link Iterable} object retrieved from component runtime classes
     * @return data {@link Iterable} object after processing
     */
    abstract Iterable<Object> processDataIterable(Iterable<Object> dataIterable);
}
