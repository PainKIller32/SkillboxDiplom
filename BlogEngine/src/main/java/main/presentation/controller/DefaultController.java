package main.presentation.controller;

import main.domain.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
    private final FileService fileService;

    public DefaultController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping(value = {
            "/edit/*",
            "/calendar/*",
            "/my/*",
            "/login",
            "/login/*",
            "/moderator/*",
            "/moderation/*",
            "/post/*",
            "/posts/*",
            "/profile",
            "settings",
            "/stat",
            "/404"
    })
    public String frontend() {
        return "forward:/";
    }

    @GetMapping("/upload/{firstDir}/{secondDir}/{thirdDir}/{fileName:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable("firstDir") String firstDir,
                                   @PathVariable("secondDir") String secondDir,
                                   @PathVariable("thirdDir") String thirdDir,
                                   @PathVariable("fileName") String fileName) {
        String path = "/" + firstDir + "/" + secondDir + "/" + thirdDir + "/" + fileName;
        byte[] image = fileService.getImage(path);
        if (image.length == 0) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
}