package com.aiqing.imagepicker.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;


public class ReadableMapUtils {
    public static
    @NonNull
    boolean hasAndNotEmpty(
            @NonNull final Bundle target,
            @NonNull final String key) {
        if (!target.containsKey(key)) {
            return false;
        }

        if (target.get(key) == null) {
            return false;
        }

        final String value = target.getString(key);
        return !TextUtils.isEmpty(value);
    }


    public static
    @NonNull
    boolean hasAndNotNullReadableMap(@NonNull final Bundle target,
                                     @NonNull final String key) {
        return hasAndNotEmpty(target, key);
    }


    public static
    @NonNull
    boolean hasAndNotEmptyString(@NonNull final Bundle target,
                                 @NonNull final String key) {
        return hasAndNotEmpty(target, key);
    }
}