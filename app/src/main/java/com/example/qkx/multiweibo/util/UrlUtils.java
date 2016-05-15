package com.example.qkx.multiweibo.util;

/**
 * Created by qkx on 16/3/8.
 */
public class UrlUtils {
    public static String fromThumbnail2Bmiddle(String thumbnailUrl) {
        String[] s = thumbnailUrl.split("thumbnail");
        return s[0] + "bmiddle" + s[1];
    }

    public static String fromThumbnail2Original(String thumbnailUrl) {
        String[] s = thumbnailUrl.split("thumbnail");
        return s[0] + "large" + s[1];
    }
}
