package com.wkclz.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<String> getFileList(List<String> filesResult, String strPath) {

        if (filesResult==null){
            filesResult = new ArrayList<>();
        }

        File dir = new File(strPath);
        if (!dir.exists()){
            return filesResult;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String absolutePath = files[i].getAbsolutePath();
                if (files[i].isDirectory()) {
                    getFileList(filesResult,absolutePath);
                } else {
                    filesResult.add(absolutePath);
                }
            }
        }
        return filesResult;
    }


    /**
     * 读取文件
     * @param path
     * @return
     */
    public static String readFile(String path){
        File file = new File(path);
        return readFile(file);
    }

    /**
     * 读取文件
     * @param file
     * @return
     */
    public static String readFile(File file){
        try {
            if (!file.isFile()){
                throw new RuntimeException("error file!");
            }
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String s = "";
            while ((s =bReader.readLine()) != null) {
                sb.append(s + "\n");
            }
            bReader.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
