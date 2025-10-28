package com.citi.reports.util;

import java.io.File;

public class FileUtils {

    public static File validateLocalFile(String path) {
        File f = new File(path);
        if (!f.exists()) {
            throw new IllegalArgumentException("File not found: " + path);
        }
        return f;
    }
}
