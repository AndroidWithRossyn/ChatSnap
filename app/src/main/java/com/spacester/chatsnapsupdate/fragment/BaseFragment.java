package com.spacester.chatsnapsupdate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressWarnings("ALL")
public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRoot = inflater.inflate(getLayoutRedId(), container, false);
        inOnCreateView(mRoot, container, savedInstanceState);
        return mRoot;
    }
    @LayoutRes
    public abstract int getLayoutRedId();
    @SuppressWarnings("EmptyMethod")
    public abstract void inOnCreateView(View root, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}
