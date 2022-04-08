package com.github.zfy.serialize;

/**
 * @author zfy
 * Date: 2022.4.8
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String msg) {
        super(msg);
    }
    public SerializeException(Throwable throwable){ super(throwable);}
}