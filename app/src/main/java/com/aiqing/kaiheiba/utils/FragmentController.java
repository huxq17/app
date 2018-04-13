package com.aiqing.kaiheiba.utils;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

public class FragmentController<T extends Fragment> {
    private FragmentManager fragmentManager;
    public static final String ID = "id";

    public FragmentController(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public <R extends T> R getFragmentById(int id, Class<R> clazz, Supplier<R> initial) {
        R fragment = null;
        List<Fragment> fs = fragmentManager.getFragments();
        if (fs != null) {
            for (Fragment f : fs) {
                if (f.getClass() == clazz) {
                    T baseFragment = (T) f;
                    Bundle args = baseFragment.getArguments();
                    if (args != null && args.getInt(ID) == id) {
                        fragment = (R) baseFragment;
                        break;
                    }
                }
            }
        }
        if (fragment == null && initial != null) {
            fragment = initial.get();
            Bundle args = fragment.getArguments();
            args = args == null ? new Bundle() : args;
            args.putInt(ID, id);
            fragment.setArguments(args);
        }
        return fragment;
    }

    public void show(Fragment fragment, @IdRes int contentId) {
        FragmentManager fm = fragmentManager;
        FragmentTransaction transaction = fm.beginTransaction();
        List<Fragment> fs = fm.getFragments();
        if (fragment.isAdded()) {
            transaction.show(fragment);
        } else {
            transaction.add(contentId, fragment);
        }
        for (Fragment f : fs) {
            if (fragment == f) {
//                transaction.show(f);
            } else {
                transaction.hide(f);
            }
        }
        transaction.commit();
    }

    public interface Supplier<T> {
        T get();
    }

}
