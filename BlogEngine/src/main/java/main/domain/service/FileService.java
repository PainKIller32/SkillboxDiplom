package main.domain.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

@Service
@Transactional
public class FileService {
    public boolean deleteFile(File file) {
        try {
            Files.walk(file.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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