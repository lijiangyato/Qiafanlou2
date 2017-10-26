package com.ings.gogo.utils;

import android.widget.EditText;

/**
 * <h3>Description</h3>
 * TODO
 * <h3>Author</h3> lzb
 * <h3>Date</h3> 2016/3/8 17:55
 * <h3>Copyright</h3> Copyright (c)2016 Shenzhen Tentinet Technology Co., Ltd. Inc. All rights reserved.
 */
public class AMapUtil {
    /**
     * 判断edittext是否null
     */
    public static String checkEditText(EditText editText) {
        if (editText != null && editText.getText() != null
                && !(editText.getText().toString().trim().equals(""))) {
            return editText.getText().toString().trim();
        } else {
            return "";
        }
    }
}
