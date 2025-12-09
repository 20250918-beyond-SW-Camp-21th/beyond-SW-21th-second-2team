package com.whatthefork.communicationandalarm.common.utils;

import java.util.List;

public class PageUtil {

    public static boolean hasNext(List<?> contents, int size) {
        return contents.size() == size;
    }

    public static int nextOffset(int offset, int size) {
        return offset + size;
    }
}

