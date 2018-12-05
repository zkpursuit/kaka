package com.kaka.util;

/**
 *
 * @author zhoukai
 */
public class TypeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TypeException() {
        super();
    }

    public TypeException(String message) {
        super(message);
    }

    public TypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
