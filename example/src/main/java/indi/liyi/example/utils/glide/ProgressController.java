package indi.liyi.example.utils.glide;


import java.util.HashMap;
import java.util.Map;

public class ProgressController {
    private static final Map<Object, OnProgressListener> LISTENER_MAP = new HashMap<>();

    /**
     * 注册进度监听
     */
    public static void registerListener(Object tag, OnProgressListener listener) {
        LISTENER_MAP.put(tag, listener);
    }

    /**
     * 获取进度监听
     */
    public static OnProgressListener getListener(Object tag) {
        return LISTENER_MAP.get(tag);
    }

    /**
     * 取消注册进度监听
     */
    public static void unregisterListener(Object tag) {
        LISTENER_MAP.remove(tag);
    }
}
