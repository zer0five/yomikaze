package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;

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
        String[] ids = images.stream().map(Snowflake::toString).toArray(String[]::new);
        if (ids.length == 1) {
            return ResponseEntity.created(URI.create("/image/" + ids[0])).body(ids);
        }
        return ResponseEntity.ok(ids);
    }

    private ResponseEntity<Resource> getFile(String id, String name) throws SQLException {
        return getFile(null, id, name);
    }

    private ResponseEntity<Resource> getFile(String owner, String id, String name) {
        Snowflake snowflake = Snowflake.of(id);
        Snowflake ownerSnowflake = Snowflake.of(owner);
        Optional<Image> file = imageRepository.findByIdAndOwner_IdAndNameLikeIgnoreCase(snowflake, ownerSnowflake, name);
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Image actualFile = file.get();
        Blob data = actualFile.getData();
        try {
            if (data == null || data.length() == 0) {
                return ResponseEntity.noContent().build();
            }
            MediaType mediaType = actualFile.getMediaType();
            Resource resource = new InputStreamResource(data.getBinaryStream());
            return ResponseEntity.ok()
                .contentType(mediaType)
                .contentLength(data.length())
                .body(resource);
        } catch (SQLException e) {
            log.error("Error while reading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/image/{owner}/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Resource> getAttachment(@PathVariable("owner") String owner, @PathVariable("id") String id, @PathVariable("name") String name) throws SQLException {
        return getFile(owner, id, name);
    }

    @GetMapping("/image/{id}")
    public String getAttachment(@PathVariable("id") String id) {
        Snowflake snowflake = Snowflake.of(id);
        Image file = imageRepository.findById(snowflake).orElseThrow(() -> new EntityNotFoundException("File not found"));
        Snowflake owner = file.getOwner().getId();
        return MessageFormat.format("redirect:/image/{0}/{1}/{2}", owner, id, file.getName());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

}
