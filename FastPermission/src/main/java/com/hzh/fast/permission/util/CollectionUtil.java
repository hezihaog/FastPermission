package com.hzh.fast.permission.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

/**
 * Created by Hezihao on 2017/7/10.
 * 集合工具类
 */

public class CollectionUtil {
    /**
     * Returns a {@link java.util.Queue} of the given size using Glide's preferred implementation.
     */
    public static <T> Queue<T> createQueue(int size) {
        return new ArrayDeque<T>(size);
    }

    /**
     * Returns a copy of the given list that is safe to iterate over and perform actions that may
     * modify the original list.
     *
     * <p> See #303 and #375. </p>
     */
    public static <T> List<T> getSnapshot(Collection<T> other) {
        // toArray creates a new ArrayList internally and this way we can guarantee entries will not
        // be null. See #322.
        List<T> result = new ArrayList<T>(other.size());
        for (T item : other) {
            result.add(item);
        }
        return result;
    }
}
