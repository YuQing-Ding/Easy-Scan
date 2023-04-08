/*
 * Copyright (C) 2019 Easy Software. All Rights Reserved.
 */
package com.EasyCN.Easy.Scan;

import java.io.File;

import android.content.Context;
//文件的操作工具类
public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }
}
