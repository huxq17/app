package com.aiqing.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.webkit.MimeTypeMap;

import com.aiqing.imagepicker.media.ImageConfig;
import com.aiqing.imagepicker.permissions.PermissionUtils;
import com.aiqing.imagepicker.utils.MediaUtils;
import com.aiqing.imagepicker.utils.RealPathUtil;
import com.aiqing.imagepicker.utils.UI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static com.aiqing.imagepicker.utils.MediaUtils.createNewFile;
import static com.aiqing.imagepicker.utils.MediaUtils.fileScan;
import static com.aiqing.imagepicker.utils.MediaUtils.getResizedImage;
import static com.aiqing.imagepicker.utils.MediaUtils.readExifInterface;
import static com.aiqing.imagepicker.utils.MediaUtils.removeUselessFiles;
import static com.aiqing.imagepicker.utils.MediaUtils.rolloutPhotoFromCamera;

public class ImagePicker {

    public static final int REQUEST_LAUNCH_IMAGE_CAPTURE = 13001;
    public static final int REQUEST_LAUNCH_IMAGE_LIBRARY = 13002;
    public static final int REQUEST_LAUNCH_VIDEO_LIBRARY = 13003;
    public static final int REQUEST_LAUNCH_VIDEO_CAPTURE = 13004;
    public static final int REQUEST_PERMISSIONS_FOR_CAMERA = 14001;
    public static final int REQUEST_PERMISSIONS_FOR_LIBRARY = 14002;

    private Application application = null;
    private final int dialogThemeId = R.style.DefaultExplainingPermissionsTheme;
    private Activity activity;
    private Bundle options;
    protected Uri cameraCaptureURI;
    private Boolean noData = false;
    private Boolean pickVideo = false;
    private ImageConfig imageConfig = new ImageConfig(null, null, 0, 0, 100, 0, false);


    @Deprecated
    private int videoQuality = 1;

    @Deprecated
    private int videoDurationLimit = 0;
    OnImagePickerListener imagePickerListener;

    public Activity getActivity() {
        return activity;
    }

    public ImagePicker(Activity activity, OnImagePickerListener listener) {
        this.activity = activity;
        imagePickerListener = listener;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionsGranted = true;

        for (int i = 0; i < permissions.length; i++) {
            final boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            permissionsGranted = permissionsGranted && granted;
        }

        if (imagePickerListener == null || options == null) {
            return;
        }

        if (!permissionsGranted) {
            imagePickerListener.onError("Permissions weren't granted");
            boolean isCamera = requestCode == REQUEST_PERMISSIONS_FOR_CAMERA;
            final Boolean dontAskAgain = isCamera ? !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                    : !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (dontAskAgain) {
                String title = isCamera ? "拍照" : "打开相册";
                String content = String.format("缺少%s所需的%s权限", isCamera ? new Object[]{"拍照", "存储和相机"} : new Object[]{"打开相册", "存储"});
                final AlertDialog dialog = PermissionUtils
                        .explainingDialog(this, title, content, new PermissionUtils.OnExplainingPermissionCallback() {
                            @Override
                            public void onCancel(WeakReference<ImagePicker> moduleInstance,
                                                 DialogInterface dialogInterface) {
                                final ImagePicker module = moduleInstance.get();
                                if (module == null) {
                                    return;
                                }
                                module.doOnCancel();
                            }

                            @Override
                            public void onReTry(WeakReference<ImagePicker> moduleInstance,
                                                DialogInterface dialogInterface) {
                                final ImagePicker module = moduleInstance.get();
                                if (module == null) {
                                    return;
                                }
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", module.getActivity().getPackageName(), null);
                                intent.setData(uri);
                                final Activity innerActivity = module.activity;
                                if (innerActivity == null) {
                                    return;
                                }
                                innerActivity.startActivityForResult(intent, 1);
                            }
                        });
                dialog.show();
            }
            return;
        }

        switch (requestCode) {
            case REQUEST_PERMISSIONS_FOR_CAMERA:
                launchCamera(options);
                break;

            case REQUEST_PERMISSIONS_FOR_LIBRARY:
                launchImageLibrary(options);
                break;

        }
    }

    public void showImagePicker() {
        showImagePicker(getDefaultOptions());
    }

    public Bundle getDefaultOptions() {
        Bundle bundle = new Bundle();
        bundle.putString("title", "选择照片");
        bundle.putString("takePhotoButtonTitle", "拍照");
        bundle.putString("chooseFromLibraryButtonTitle", "相册");
        bundle.putString("cancelButtonTitle", "取消");
        return bundle;
    }

    public void showImagePicker(final Bundle options) {
        Activity currentActivity = activity;
        application = activity.getApplication();
        if (currentActivity == null) {
            imagePickerListener.onError("can't find current Activity");
            return;
        }
        this.options = options;
        imageConfig = new ImageConfig(null, null, 0, 0, 100, 0, false);

        final AlertDialog dialog = UI.chooseDialog(this, options, new UI.OnAction() {
            @Override
            public void onTakePhoto(@NonNull final ImagePicker module) {
                if (module == null) {
                    return;
                }
                module.launchCamera();
            }

            @Override
            public void onUseLibrary(@NonNull final ImagePicker module) {
                if (module == null) {
                    return;
                }
                module.launchImageLibrary();
            }

            @Override
            public void onCancel(@NonNull final ImagePicker module) {
                if (module == null) {
                    return;
                }
                module.doOnCancel();
            }
        });
        dialog.show();
    }

    public void doOnCancel() {
        imagePickerListener.onCancel();
    }

    public void launchCamera() {
        this.launchCamera(this.options);
    }

    // NOTE: Currently not reentrant / doesn't support concurrent requests
    public void launchCamera(final Bundle options) {
        application = activity.getApplication();
        if (!isCameraAvailable()) {
            imagePickerListener.onError("Camera not available");
            return;
        }

        final Activity currentActivity = activity;
        if (currentActivity == null) {
            imagePickerListener.onError("can't find current Activity");
            return;
        }

        this.options = options;

        if (!permissionsCheck(currentActivity, REQUEST_PERMISSIONS_FOR_CAMERA)) {
            return;
        }

        parseOptions(this.options);

        int requestCode;
        Intent cameraIntent;

        if (pickVideo) {
            requestCode = REQUEST_LAUNCH_VIDEO_CAPTURE;
            cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, videoQuality);
            if (videoDurationLimit > 0) {
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, videoDurationLimit);
            }
        } else {
            requestCode = REQUEST_LAUNCH_IMAGE_CAPTURE;
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            final File original = createNewFile(application, this.options, false);
            imageConfig = imageConfig.withOriginalFile(original);

            cameraCaptureURI = RealPathUtil.compatUriFromFile(application, imageConfig.original);
            if (cameraCaptureURI == null) {
                imagePickerListener.onError("Couldn't get file path for photo");
                return;
            }
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraCaptureURI);
        }

        if (cameraIntent.resolveActivity(application.getPackageManager()) == null) {
            imagePickerListener.onError("Cannot launch camera");
            return;
        }

        try {
            currentActivity.startActivityForResult(cameraIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            imagePickerListener.onError("Cannot launch camera");
        }
    }

    public void launchImageLibrary() {
        this.launchImageLibrary(this.options);
    }

    // NOTE: Currently not reentrant / doesn't support concurrent requests
    public void launchImageLibrary(final Bundle options) {
        application = activity.getApplication();
        final Activity currentActivity = activity;
        if (currentActivity == null) {
            imagePickerListener.onError("can't find current Activity");
            return;
        }

        this.options = options;

        if (!permissionsCheck(currentActivity, REQUEST_PERMISSIONS_FOR_LIBRARY)) {
            return;
        }

        parseOptions(this.options);

        int requestCode;
        Intent libraryIntent;
        if (pickVideo) {
            requestCode = REQUEST_LAUNCH_VIDEO_LIBRARY;
            libraryIntent = new Intent(Intent.ACTION_PICK);
            libraryIntent.setType("video/*");
        } else {
            requestCode = REQUEST_LAUNCH_IMAGE_LIBRARY;
            libraryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }

        if (libraryIntent.resolveActivity(application.getPackageManager()) == null) {
            imagePickerListener.onError("Cannot launch photo library");
            return;
        }
        try {
            currentActivity.startActivityForResult(libraryIntent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            imagePickerListener.onError("Cannot launch photo library");
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //robustness code
        if (passResult(requestCode)) {
            return;
        }

        Bundle bundle = new Bundle();
        if (resultCode != Activity.RESULT_OK) {
            removeUselessFiles(requestCode, imageConfig);
            imagePickerListener.onCancel();
            return;
        }

        Uri uri = null;
        switch (requestCode) {
            case REQUEST_LAUNCH_IMAGE_CAPTURE:
                uri = cameraCaptureURI;
                break;

            case REQUEST_LAUNCH_IMAGE_LIBRARY:
                uri = data.getData();
                String realPath = getRealPathFromURI(uri);
                final boolean isUrl = !TextUtils.isEmpty(realPath) &&
                        Patterns.WEB_URL.matcher(realPath).matches();
                if (realPath == null || isUrl) {
                    try {
                        File file = createFileFromURI(uri);
                        realPath = file.getAbsolutePath();
                        uri = Uri.fromFile(file);
                    } catch (Exception e) {
                        imagePickerListener.onError("Could not read photo");
                        return;
                    }
                }
                imageConfig = imageConfig.withOriginalFile(new File(realPath));
                break;

            case REQUEST_LAUNCH_VIDEO_LIBRARY:
                bundle.putString("uri", data.getData().toString());
                bundle.putString("path", getRealPathFromURI(data.getData()));
                imagePickerListener.onSuccess(bundle);
                return;

            case REQUEST_LAUNCH_VIDEO_CAPTURE:
                final String path = getRealPathFromURI(data.getData());
                fileScan(application, path);
                bundle.putString("uri", data.getData().toString());
                bundle.putString("path", getRealPathFromURI(data.getData()));
                imagePickerListener.onSuccess(bundle);
                return;
        }

        final MediaUtils.ReadExifResult result = readExifInterface(bundle, imageConfig);

        if (result.error != null) {
            removeUselessFiles(requestCode, imageConfig);
            imagePickerListener.onError(result.error.getMessage());
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageConfig.original.getAbsolutePath(), options);
        int initialWidth = options.outWidth;
        int initialHeight = options.outHeight;
        updatedResultResponse(bundle, uri, imageConfig.original.getAbsolutePath());
        // don't create a new file if contraint are respected
        if (imageConfig.useOriginal(initialWidth, initialHeight, result.currentRotation)) {
            bundle.putInt("width", initialWidth);
            bundle.putInt("height", initialHeight);
            fileScan(application, imageConfig.original.getAbsolutePath());
        } else {
            imageConfig = getResizedImage(application, this.options, imageConfig, initialWidth, initialHeight, requestCode);
            if (imageConfig.resized == null) {
                removeUselessFiles(requestCode, imageConfig);
                bundle.putString("error", "Can't resize the image");
            } else {
                uri = Uri.fromFile(imageConfig.resized);
                BitmapFactory.decodeFile(imageConfig.resized.getAbsolutePath(), options);
                bundle.putInt("width", options.outWidth);
                bundle.putInt("height", options.outHeight);

                updatedResultResponse(bundle, uri, imageConfig.resized.getAbsolutePath());
                fileScan(application, imageConfig.resized.getAbsolutePath());
            }
        }

        if (imageConfig.saveToCameraRoll && requestCode == REQUEST_LAUNCH_IMAGE_CAPTURE) {
            final MediaUtils.RolloutPhotoResult rolloutResult = rolloutPhotoFromCamera(imageConfig);

            if (rolloutResult.error == null) {
                imageConfig = rolloutResult.imageConfig;
                uri = Uri.fromFile(imageConfig.getActualFile());
                updatedResultResponse(bundle, uri, imageConfig.getActualFile().getAbsolutePath());
            } else {
                removeUselessFiles(requestCode, imageConfig);
                final String errorMessage = new StringBuilder("Error moving image to camera roll: ")
                        .append(rolloutResult.error.getMessage()).toString();
                bundle.putString("error", errorMessage);
                return;
            }
        }
        imagePickerListener.onSuccess(bundle);
        this.options = null;
    }

    public @StyleRes
    int getDialogThemeId() {
        return this.dialogThemeId;
    }


    private boolean passResult(int requestCode) {
        return imagePickerListener == null || (cameraCaptureURI == null && requestCode == REQUEST_LAUNCH_IMAGE_CAPTURE)
                || (requestCode != REQUEST_LAUNCH_IMAGE_CAPTURE && requestCode != REQUEST_LAUNCH_IMAGE_LIBRARY
                && requestCode != REQUEST_LAUNCH_VIDEO_LIBRARY && requestCode != REQUEST_LAUNCH_VIDEO_CAPTURE);
    }

    private void updatedResultResponse(Bundle bundle, final Uri uri,
                                       final String path) {
        bundle.putString("uri", uri.toString());
        bundle.putString("path", path);

//        if (!noData) {
//            responseHelper.putString("data", getBase64StringFromFile(path));
//        }

        putExtraFileInfo(path, bundle);
    }

    private boolean permissionsCheck(@NonNull final Activity activity, @NonNull final int requestCode) {
        final int writePermission = ActivityCompat
                .checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        final int cameraPermission = ActivityCompat
                .checkSelfPermission(activity, Manifest.permission.CAMERA);
        boolean isCamera = requestCode == REQUEST_PERMISSIONS_FOR_CAMERA;
        final boolean permissionsGrated = isCamera ? writePermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED : writePermission == PackageManager.PERMISSION_GRANTED;
        if (!permissionsGrated) {
            String[] PERMISSIONS = isCamera ? new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}
                    : new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (activity instanceof Activity) {
//                    (activity).requestPermissions(PERMISSIONS, requestCode);
                ActivityCompat.requestPermissions(activity, PERMISSIONS, requestCode);
            }
            return false;
        }
        return true;
    }

    private boolean isCameraAvailable() {
        return application.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || application.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private
    @NonNull
    String getRealPathFromURI(@NonNull final Uri uri) {
        return RealPathUtil.getRealPathFromURI(application, uri);
    }

    /**
     * Create a file from uri to allow image picking of image in disk cache
     * (Exemple: facebook image, google image etc..)
     *
     * @param uri
     * @return File
     * @throws Exception
     * @doc =>
     * https://github.com/nostra13/Android-Universal-Image-Loader#load--display-task-flow
     */
    private File createFileFromURI(Uri uri) throws Exception {
        File file = new File(application.getExternalCacheDir(), "photo-" + uri.getLastPathSegment());
        InputStream input = application.getContentResolver().openInputStream(uri);
        OutputStream output = new FileOutputStream(file);

        try {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } finally {
            output.close();
            input.close();
        }

        return file;
    }

    private String getBase64StringFromFile(String absoluteFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(absoluteFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    private void putExtraFileInfo(final String path, final Bundle responseHelper) {
        // size && filename
        try {
            File f = new File(path);
            responseHelper.putDouble("fileSize", f.length());
            responseHelper.putString("fileName", f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // type
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            responseHelper.putString("type", MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
        }
    }

    public interface OnImagePickerListener {
        void onError(String error);

        void onSuccess(Bundle response);

        void onCancel();
    }

    private void parseOptions(final Bundle options) {
        noData = false;
        if (options.containsKey("noData")) {
            noData = options.getBoolean("noData");
        }
        imageConfig = imageConfig.updateFromOptions(options);
        pickVideo = false;
        if (options.containsKey("mediaType") && options.getString("mediaType").equals("video")) {
            pickVideo = true;
        }
        videoQuality = 1;
        if (options.containsKey("videoQuality") && options.getString("videoQuality").equals("low")) {
            videoQuality = 0;
        }
        videoDurationLimit = 0;
        if (options.containsKey("durationLimit")) {
            videoDurationLimit = options.getInt("durationLimit");
        }
    }
}
