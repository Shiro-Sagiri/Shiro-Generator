package com.shiro.web.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Shiro
 */
public class NamedThreadFactory implements ThreadFactory {

    private String namePrefix;
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    public NamedThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r,namePrefix + "-thread-" + atomicInteger.getAndIncrement());
    }

}
