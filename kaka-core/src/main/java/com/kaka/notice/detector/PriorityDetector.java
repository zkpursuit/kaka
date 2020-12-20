package com.kaka.notice.detector;

/**
 * @author zkpursuit
 */
abstract public class PriorityDetector implements IDetector {

    protected static class Element {
        private final Object annotation;
        private final Class<?> clasz;

        public Element(Object annotation, Class<?> clasz) {
            this.annotation = annotation;
            this.clasz = clasz;
        }

        public <A> A getAnnotation() {
            return (A) annotation;
        }

        public Class<?> getClasz() {
            return clasz;
        }
    }

    abstract public void centralizeProcess();
}
