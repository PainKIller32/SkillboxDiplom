package main.domain.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class FileService {
    public boolean deleteFile(File file) {
        boolean result;
        if (file.isDirectory()) {
            String[] files = file.list();
            if (files == null || files.length == 0) {
                result = file.delete();
            } else {
                for (String fileName : files) {
                    deleteFile(new File(file.getPath() + File.separator + fileName));
                }
                result = file.delete();
            }
        } else {
            result = file.delete();
        }
        return result;
    }

    @Value("${uploadPath}")
    String uploadPath;

    public String uploadImage(MultipartFile file) {
        String savePathHash = RandomStringUtils.randomAlphabetic(6);
        String savePath = uploadPath +
                savePathHash.substring(0, 2) +
                "/" +
                savePathHash.substring(2, 4) +
                "/" +
                savePathHash.substring(4, 6) +
                "/";
        File saveFile = new File(savePath);
        if (saveFile.mkdirs()) {
            savePath += RandomStringUtils.randomAlphanumeric(5) + ".jpg";
            saveFile = new File(savePath);
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(saveFile))) {
                stream.write(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return "/" + savePath;
    }

    public byte[] getImage(String path) {
        File file = new File(uploadPath + path);
        byte[] image = new byte[0];
        if (file.exists()) {
            try (FileInputStream is = new FileInputStream(file)) {
                image = is.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
                image = new byte[0];
            }
        }
        return image;
    }
}