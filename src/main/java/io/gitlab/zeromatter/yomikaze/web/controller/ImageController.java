package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.persistence.repository.DBFileRepository;
import io.gitlab.zeromatter.yomikaze.service.DatabaseFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final DatabaseFileStorageService fileStorageService;
    private final DBFileRepository dbFileRepository;

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/upload")
    public ModelAndView upload(@RequestParam("files") List<MultipartFile> files, ModelAndView model) {
        model.setViewName("upload");
        if (files.isEmpty()) {
            model.addObject("failure", true);
            return model;
        }
        int i = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                model.addObject("failure", true);
                return model;
            }
            UUID id = fileStorageService.storeFile(file);
            System.out.printf("files[%d] = { id: '%s', name: '%s', type: '%s', size: %d }%n", i++, id, file.getOriginalFilename(), file.getContentType(), file.getSize());
        }
        model.addObject("success", true);
        return model;
    }

    @GetMapping("/{id}")
    public String getImage(@PathVariable("id") String id) {
        return "redirect:/image/" + id + "/" + dbFileRepository.findById(UUID.fromString(id)).map(DBFile::getName).orElse(UUID.randomUUID().toString());
    }

    @GetMapping("/{id}/{name}")
    @ResponseBody
    public ResponseEntity<?> getImage(@PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(id, name);
    }

    private ResponseEntity<?> getFile(String id, String name) {
        UUID uuid = UUID.fromString(id);
        Optional<DBFile> file = dbFileRepository.findById(uuid);
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        if (name != null && !name.equals(file.get().getName())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.get().getType()))
                .body(file.get().getData());
    }

}
