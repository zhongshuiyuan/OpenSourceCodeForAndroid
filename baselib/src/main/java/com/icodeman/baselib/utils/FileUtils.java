package com.icodeman.baselib.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LMW on 2017/12/20.
 */
public class FileUtils {

    /**
     * 获取文件
     *
     * @param fileName
     * @param createWhenNotExit 如果获取不到是否创建文件
     *                          true : 当获取不到文件时，创建文件并返回
     *                          false : 当获取不到文件时，返回null
     * @return
     */
    public static File getFile(String fileName, boolean createWhenNotExit) {
        File file = new File(fileName);
        if (file.exists()) {
            return file;
        } else {
            if (createWhenNotExit) {
                return createFile(fileName);
            } else {
                return null;
            }
        }
    }

    /**
     * 创建文件  当文件存在是返回null
     *
     * @param fileName
     * @return
     */
    public static File createFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("文件创建失败 : " + e.getMessage());
            }
            return file;
        } else {
            return null;
        }
    }

    /**
     * 拷贝文件
     *
     * @param srcPath
     * @param destPath
     * @throws IOException
     */
    public static void copyFile(String srcPath, String destPath) throws IOException {

        // 打开输入流
        FileInputStream fis = new FileInputStream(srcPath);
        // 打开输出流
        FileOutputStream fos = new FileOutputStream(destPath);

        // 读取和写入信息
        int len = 0;
        // 创建一个字节数组，当做缓冲区
        byte[] b = new byte[1024];
        while ((len = fis.read(b)) != -1) {
            fos.write(b,0,len);
        }
        fos.flush();
        // 关闭流  先开后关  后开先关
        fos.close(); // 后开先关
        fis.close(); // 先开后关
    }

    /**
     * 获取文件夹下所有的文件路径
     *
     * @param path 文件夹路径
     * @return
     */
    public static List<String> getFilesFromPath(String path) {
        List<String> filePaths = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File tFile : files) {
                    filePaths.addAll(getFilesFromPath(tFile.getAbsolutePath()));
                }
            } else {
                filePaths.add(file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    /**
     * 获取文件拓展名
     * @param filename  路径或者文件名
     * @return
     */
    public static String getFileSuffix(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取不带后缀的文件名
     *
     * @param filename  路径或者文件名
     * @return
     */
    public static String getFileNameNoSuffix(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }
}
