package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.DBFileRepository;
import io.gitlab.zeromatter.yomikaze.service.DatabaseFileStorageService;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DatabaseFileStorageService fileStorageService;
    private final DBFileRepository dbFileRepository;
    private final AccountRepository accountRepository;

    @PostAuthorize("hasAuthority('page.create')")
    @PreAuthorize("authentication != null && isAuthenticated()")
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
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                model.addObject("failure", true);
                return model;
            }
            Snowflake id = fileStorageService.storeFile(file, account);
            DBFile dbFile = dbFileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("File not found"));
            log.info(MessageFormat.format("Uploaded file {0} with id {1}", dbFile.getName(), dbFile.getId()));
        }
        model.addObject("success", true);
        return model;
    }

    @GetMapping("/image/{id}")
    public String getImage(@PathVariable("id") String id) {
        Snowflake snowflake = Snowflake.of(id);
        Optional<DBFile> dbFile = dbFileRepository.findById(snowflake);
        if (!dbFile.isPresent()) {
            throw new EntityNotFoundException("File not found");
        }
        return MessageFormat.format("redirect:/image/{0}/{1}", id, dbFile.get().getName());
    }

    @GetMapping("/image/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Object> getImage(@PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(id, name);
    }

    private ResponseEntity<Object> getFile(String id, String name) {
        return getFile(null, id, name);
    }

    private ResponseEntity<Object> getFile(String owner, String id, String name) {
        Snowflake snowflake = Snowflake.of(id);
        Snowflake ownerSnowflake = Snowflake.of(owner);
        Optional<DBFile> file = dbFileRepository.findByIdAndAccountIdAndName(snowflake, ownerSnowflake, name);
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DBFile actualFile = file.get();
        if (actualFile.getData() == null || actualFile.getData().length == 0) {
            return ResponseEntity.noContent().build();
        }
        MediaType mediaType = MediaType.parseMediaType(actualFile.getType());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(actualFile.getData());
    }

    @GetMapping("/attachment/{owner}/{id}/{name}")
    @ResponseBody
    public ResponseEntity<Object> getAttachment(@PathVariable("owner") String owner, @PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(owner, id, name);
    }

    @GetMapping("/attachment/{id}")
    public String getAttachment(@PathVariable("id") String id) {
        Snowflake snowflake = Snowflake.of(id);
        DBFile file = dbFileRepository.findById(snowflake).orElseThrow(() -> new EntityNotFoundException("File not found"));
        Snowflake owner = file.getOwner().getId();
        return MessageFormat.format("redirect:/attachment/{0}/{1}/{2}", owner, id, file.getName());
    }

}
