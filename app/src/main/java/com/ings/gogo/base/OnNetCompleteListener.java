package com.ings.gogo.base;

import android.util.Log;

//
public interface OnNetCompleteListener<T> {
    void onOK(T entity);
    void onError();
}
