package com.kaka.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static void getDirectories(File dir, List<File> dirs) {
        if (dir.isDirectory()) {
            dirs.add(dir);
            File[] files = dir.listFiles(file -> file.isDirectory());
            if (files.length > 0) {
                for (File file : files) {
                    getDirectories(file, dirs);
                }
            }
        }
    }

    /**
     * 获取目录下的目录文件
     *
     * @param dir         初始目录文件
     * @param descendants true表示获取初始目录下的所有子孙级目录
     * @return 目录文件集合，第一个元素为初始目录文件
     */
    public static List<File> getDirectories(File dir, boolean descendants) {
        List<File> dirs = new ArrayList<>();
        if (!descendants) {
            dirs.add(dir);
            File[] files = dir.listFiles(file -> file.isDirectory());
            if (files.length > 0) {
                for (File file : files) {
                    dirs.add(file);
                }
            }
            return dirs;
        }
        getDirectories(dir, dirs);
        return dirs;
    }

    private FileUtils() {
    }
}
