package com.hualiang.model.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;

public class FileUtils {
    /**
     * 删除目录及目录中的子文件（递归）
     *
     * @param path 路径
     * @return
     */
    public static boolean deleteDir(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                deleteDir(path + "/" + child);
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static String renameDir(String path, String newName) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            String pathname = path.substring(0, path.lastIndexOf("\\") + 1) + newName;
            dir.renameTo(new File(pathname));
            return pathname;
        }
        return null;
    }

    public static void copyDir(File sourceDir, File destinationDir) throws IOException {
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        File[] files = sourceDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destinationFile = new File(destinationDir, file.getName());

                if (file.isDirectory()) {
                    copyDir(file, destinationFile);
                } else {
                    Files.copy(file.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    public static String getOneFile(String dirPath, String fileName) {
        File directory = new File(dirPath);
        File[] files = directory.listFiles();

        for (File file : files) {
            String fileWithoutExtension = file.getName().replaceFirst("[.][^.]+$", "");
            if (fileWithoutExtension.equals(fileName)) {
                return file.getAbsolutePath();
            }
        }
        if (files.length > 0 && files[0].isFile()) {
            File firstFile = files[0];
            String name = firstFile.getName();
            File renamedFile = new File(directory, fileName + "." + name.substring(name.lastIndexOf(".") + 1));
            boolean success = firstFile.renameTo(renamedFile);
            if (success) {
                return renamedFile.getAbsolutePath();
            }
        }
        return null;
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> result = new ArrayList<>();

        int partitions = (int) Math.ceil((double) list.size() / size);

        for (int i = 0; i < partitions; i++) {
            int start = i * size;
            int end = Math.min(start + size, list.size());

            List<T> subList = new ArrayList<>(list.subList(start, end));
            result.add(subList);
        }

        return result;
    }

    public static String pathToUrl(String path) {
        return "/" + path.substring(path.indexOf("static")).replace("\\", "/");
    }
}
