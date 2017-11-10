package com.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.io.FileUtils;

public class FileManager {
    private File rootDir;
    private File[] files;

    public FileManager (String folder) {
        // Cria referencia para a pasta do servidor
        this.rootDir = new File(folder);

        // Cria pasta caso não exista
        if (!rootDir.exists()) {
            System.out.println("Creating directory: " + this.rootDir.getName());

            boolean result = false;

            try{
                this.rootDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.println("Failed to create directory: " + this.rootDir.getName() +
                " with error " + se.getMessage());
            }
            if(result) {
                System.out.println("Directory created");
            }
        }

        this.files = this.rootDir.listFiles();
    }

    public String listFiles() {
        String fileList = "";
        this.files = this.rootDir.listFiles();

        for (File file : this.files) {
            fileList += file.getName() + ",";
        }

        if (fileList != "") {
            fileList = fileList.substring(0, fileList.lastIndexOf(","));
        }

        return fileList;
    }

    public String getFile(String filename) {
        for (File file : this.files) {
            if (file.getName().equals(filename)) {
                String fileEncoded = this.fileToString(file);
                return filename + "," + fileEncoded;
            }
        }

        return null;
    }

    public Boolean saveFile(String filename, String fileString) {
        try {
            File file = new File(this.rootDir + "/" + filename);

            byte[] fileData = Base64.decode(fileString);
            FileUtils.writeByteArrayToFile(file, fileData);

            this.files = this.rootDir.listFiles();
            return true;
        } catch (IOException e) {
            System.out.println(e);
        }

        this.files = this.rootDir.listFiles();
        return false;
    }

    public static String fileToString(File file) {
        try {
            byte[] fileData;
            fileData = FileUtils.readFileToByteArray(file);
            String fileEncoded = new String(Base64.encode(fileData));
            return fileEncoded;
        } catch (IOException e) {
            return null;
        }
    }

    public static File stringToFile(String filestring, String filename, String path) {
        try {
            File file = new File(path + "/" + filename);

            byte[] fileData = Base64.decode(filestring);
            FileUtils.writeByteArrayToFile(file, fileData);

            return file;
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }
}