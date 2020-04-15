package main.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
public class DefaultController {
    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/**")
    public String anotherUrl() {
        return "index";
    }

    @RequestMapping("/*")
    public String notFound(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "index";
    }

    @Value("${downloadImagePath}")
    String downloadImagePath;

    @GetMapping("/upload/{firstDir}/{secondDir}/{thirdDir}/{fileName:.+}")
    public ResponseEntity getImage(@PathVariable("firstDir") String firstDir,
                                   @PathVariable("secondDir") String secondDir,
                                   @PathVariable("thirdDir") String thirdDir,
                                   @PathVariable("fileName") String fileName) {
        File file = new File(downloadImagePath + "/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + fileName);
        byte[] image = new byte[0];
        if (file.exists()) {
            try (FileInputStream is = new FileInputStream(file)) {
                image = is.readAllBytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}