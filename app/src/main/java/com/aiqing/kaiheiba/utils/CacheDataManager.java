package com.aiqing.kaiheiba.utils;


import com.aiqing.kaiheiba.rxjava.RxSchedulers;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class CacheDataManager {
    private static final Comparator<File> COMPARATOR = new Comparator<File>() {
        public int compare(File file1, File file2) {
            return (int) (file2.lastModified() - file1.lastModified());
        }
    };

    public static void limitFileCount(File file, final int limit) {
        if (!file.exists()) return;
        final File[] cachedFiles = file.listFiles();
        if (cachedFiles == null) return;
        if (cachedFiles.length > limit) {
            Arrays.sort(cachedFiles, COMPARATOR);
            deleteFiles(Arrays.copyOfRange(cachedFiles, limit, cachedFiles.length));
        }
    }

    public static void deleteFiles(final File[] deleteFiles) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                boolean result = true;
                for (File deleteFile : deleteFiles) {
                    if (!deleteFile.delete()) {
                        result = false;
                    }
                }
                e.onNext(result);
            }
        }).compose(RxSchedulers.<Boolean>compose())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Exception {
                    }
                });
    }
}
