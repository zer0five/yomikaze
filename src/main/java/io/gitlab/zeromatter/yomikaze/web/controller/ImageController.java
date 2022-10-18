package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Image;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.ImageRepository;
import io.gitlab.zeromatter.yomikaze.service.DatabaseImageStorageService;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DatabaseImageStorageService imageStorageService;
    private final ImageRepository imageRepository;
    private final AccountRepository accountRepository;

    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAuthority('page.create')")
    @GetMapping("/image/upload")
    public String upload() {
        return "upload";
    }

    @PostAuthorize("hasAuthority('page.create')")
    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostMapping("/image/upload")
    public ModelAndView upload(@RequestParam("files") List<MultipartFile> files, Authentication auth, ModelAndView model) {
        model.setViewName("upload");
        if (files.isEmpty()) {
            model.addObject("failure", true);
            return model;
        }
        Account account = accountRepository.findByUsername(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Snowflake> images = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                model.addObject("success", false);
                return model;
            }
            Snowflake id = imageStorageService.storeSafe(file, account).orElseThrow(() -> new EntityNotFoundException("Image not found"));
            log.info(MessageFormat.format("Uploaded file {0} with id {1}", file.getOriginalFilename(), id));
            images.add(id);
        }
        model.addObject("success", true);
        model.addObject("images", images);
        return model;
    }

    @GetMapping("/image/{id}")
    public String getImage(@PathVariable("id") String id) {
        Snowflake snowflake = Snowflake.of(id);
        Optional<Image> dbFile = imageRepository.findById(snowflake);
        if (!dbFile.isPresent()) {
            throw new EntityNotFoundException("File not found");
        }
        return MessageFormat.format("redirect:/image/{0}/{1}", id, dbFile.get().getName());
    }

    @GetMapping("/image/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable("id") String id, @PathVariable("name") String name) throws SQLException {
        return getFile(id, name);
    }

    private ResponseEntity<Resource> getFile(String id, String name) throws SQLException {
        return getFile(null, id, name);
    }

    private ResponseEntity<Resource> getFile(String owner, String id, String name) throws SQLException {
        Snowflake snowflake = Snowflake.of(id);
        Snowflake ownerSnowflake = Snowflake.of(owner);
        Optional<Image> file = imageRepository.findByIdAndOwner_IdAndNameLikeIgnoreCase(snowflake, ownerSnowflake, name);
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Image actualFile = file.get();
        Blob data = actualFile.getData();
        if (data == null || data.length() == 0) {
            return ResponseEntity.noContent().build();
        }
        MediaType mediaType = actualFile.getMediaType();
        Resource resource = new InputStreamResource(data.getBinaryStream());
        return ResponseEntity.ok()
            .contentType(mediaType)
            .contentLength(data.length())
            .body(resource);
    }

    @GetMapping("/attachment/{owner}/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Resource> getAttachment(@PathVariable("owner") String owner, @PathVariable("id") String id, @PathVariable("name") String name) throws SQLException {
        return getFile(owner, id, name);
    }

    @GetMapping("/attachment/{id}")
    public String getAttachment(@PathVariable("id") String id) {
        Snowflake snowflake = Snowflake.of(id);
        Image file = imageRepository.findById(snowflake).orElseThrow(() -> new EntityNotFoundException("File not found"));
        Snowflake owner = file.getOwner().getId();
        return MessageFormat.format("redirect:/attachment/{0}/{1}/{2}", owner, id, file.getName());
    }

}
