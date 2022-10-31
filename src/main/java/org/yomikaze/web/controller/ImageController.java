package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Image;
import org.yomikaze.persistence.repository.ImageRepository;
import org.yomikaze.service.DatabaseImageStorageService;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DatabaseImageStorageService imageStorageService;
    private final ImageRepository imageRepository;

    @PostMapping("/api/v1/image/upload")
    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAnyAuthority('page.create', 'comic.create', 'profile.avatar')")
    public ResponseEntity<Object> upload(@RequestParam("files") List<MultipartFile> files, Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        if (files.isEmpty()) {
            result.put("error", "No file uploaded");
            return ResponseEntity.badRequest().body(result);
        }
        Account account = (Account) authentication.getPrincipal();
        List<Snowflake> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                result.put("error", "File is empty");
                return ResponseEntity.badRequest().body(result);
            }
            Snowflake id = imageStorageService.storeSafe(file, account).orElseThrow(() -> new EntityNotFoundException("Image not found"));
            log.info(MessageFormat.format("Uploaded file {0} with id {1}", file.getOriginalFilename(), id));
            images.add(id);
        }
        if (images.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<String> imageUrls = images.stream().map(id -> "/image/" + id).collect(Collectors.toList());
        if (imageUrls.size() == 1) {
            return ResponseEntity.created(URI.create(imageUrls.get(0))).body(imageUrls);
        }
        return ResponseEntity.ok(imageUrls);
    }

    private ResponseEntity<Resource> getFile(String id, String name) {
        Snowflake imageId = Snowflake.of(id);
        Optional<Image> image = imageRepository.findById(imageId);
        boolean matchName = image.map(Image::getName).map(n -> n.equals(name)).orElse(false);
        if (!image.isPresent() || !matchName) {
            return ResponseEntity.notFound().build();
        }
        return toResponse(image);
    }

    private ResponseEntity<Resource> getFile(Snowflake id) {
        Optional<Image> image = imageRepository.findById(id);
        return toResponse(image);
    }

    private ResponseEntity<Resource> getFile(String owner, String id, String name) {
        Snowflake snowflake = Snowflake.of(id);
        Snowflake ownerSnowflake = Snowflake.of(owner);
        Optional<Image> file = imageRepository.findByIdAndOwner_IdAndNameLikeIgnoreCase(snowflake, ownerSnowflake, name);
        return toResponse(file);
    }

    private ResponseEntity<Resource> toResponse(Optional<Image> file) {
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return toResponse(file.get());
    }

    private ResponseEntity<Resource> toResponse(Image file) {
        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
            .contentType(file.getMediaType())
            .contentLength(file.size())
            .body(file.toResource());

    }

    @GetMapping("/image/{owner}/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Resource> getAttachment(@PathVariable("owner") String owner, @PathVariable("id") String id, @PathVariable("name") String name) throws SQLException {
        return getFile(owner, id, name);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getAttachment(@PathVariable("id") Snowflake id) {
        return getFile(id);
    }

    @GetMapping("/image/{id}/{name}")
    public ResponseEntity<Resource> getAttachment(@PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(id, name);
    }

}
