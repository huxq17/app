package com.aiqing.imagepicker.permissions;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.aiqing.imagepicker.ImagePicker;
import com.aiqing.imagepicker.R;

import java.lang.ref.WeakReference;

public class PermissionUtils {
    public static @Nullable
    AlertDialog explainingDialog( final ImagePicker module,
                                 @NonNull final String title, String content,
                                 @NonNull final OnExplainingPermissionCallback callback) {
        final WeakReference<ImagePicker> reference = new WeakReference<>(module);

        final Activity activity = module.getActivity();

        if (activity == null) {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.DefaultExplainingPermissionsTheme);
        builder
                .setTitle(title)
                .setMessage(content)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface,
                                        int i) {
                        callback.onCancel(reference, dialogInterface);
                    }
                })
                .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int i) {
                        callback.onReTry(reference, dialogInterface);
                    }
                });

        return builder.create();
    }

    public interface OnExplainingPermissionCallback {
        void onCancel(WeakReference<ImagePicker> moduleInstance, DialogInterface dialogInterface);

        void onReTry(WeakReference<ImagePicker> moduleInstance, DialogInterface dialogInterface);
    }
}
