package com.logigear.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceableThreadLocal<T> {
    T object;
    String threadId;
    Map<String, T> objectMap = new HashMap<>();

    private String getThreadId() {
        return String.valueOf(Thread.currentThread().getId());
    }

    public void set(T object) {
        threadId = getThreadId();
        this.object = object;
        objectMap.put(threadId, object);
    }

    public T get() {
        return objectMap.get(getThreadId());
    }

    public List<T> getAll() {
        return new ArrayList<>(objectMap.values());
    }

    public void removeAll() {
        objectMap.keySet().removeIf(k -> true);
    }

    public void remove() {
        objectMap.keySet().removeIf(k -> k.equals(getThreadId()));
    }
}
