package com.aiqing.imagepicker.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ButtonsHelper {
    public static class Item {
        public final String title;
        public final String action;

        public Item(@NonNull final String title,
                    @NonNull final String action) {
            this.title = title;
            this.action = action;
        }
    }

    public final @Nullable
    Item btnCamera;
    public final @Nullable
    Item btnLibrary;
    public final @Nullable
    Item btnCancel;

    public ButtonsHelper(@Nullable final Item btnCamera,
                         @Nullable final Item btnLibrary,
                         @Nullable final Item btnCancel) {
        this.btnCamera = btnCamera;
        this.btnLibrary = btnLibrary;
        this.btnCancel = btnCancel;
    }

    public List<String> getTitles() {
        List<String> result = new LinkedList<>();

        if (btnCamera != null) {
            result.add(btnCamera.title);
        }

        if (btnLibrary != null) {
            result.add(btnLibrary.title);
        }
        return result;
    }

    public List<String> getActions() {
        List<String> result = new LinkedList<>();

        if (btnCamera != null) {
            result.add(btnCamera.action);
        }

        if (btnLibrary != null) {
            result.add(btnLibrary.action);
        }

        return result;
    }

    public static ButtonsHelper newInstance(@NonNull final Bundle options) {
        Item btnCamera = getItemFromOption(options, "takePhotoButtonTitle", "photo");
        Item btnLibrary = getItemFromOption(options, "chooseFromLibraryButtonTitle", "library");
        Item btnCancel = getItemFromOption(options, "cancelButtonTitle", "cancel");

        return new ButtonsHelper(btnCamera, btnLibrary, btnCancel);
    }

    private static @Nullable Item getItemFromOption(@NonNull final Bundle options,
                           @NonNull final String key,
                           @NonNull final String action) {
        if (!ReadableMapUtils.hasAndNotEmptyString(options, key)) {
            return null;
        }

        final String title = options.getString(key);

        return new Item(title, action);
    }
}
