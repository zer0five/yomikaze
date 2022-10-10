package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.DBFile;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.DBFileRepository;
import io.gitlab.zeromatter.yomikaze.service.DatabaseFileStorageService;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final DatabaseFileStorageService fileStorageService;
    private final DBFileRepository dbFileRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/image/upload")
    public String upload(Authentication auth) {
        System.out.println(auth.getPrincipal());
        System.out.println(auth.getAuthorities());
        System.out.println(auth.getCredentials());
        System.out.println(auth.getDetails());
        return "upload";
    }

    @PostMapping("/image/upload")
    public ModelAndView upload(@RequestParam("files") List<MultipartFile> files, Authentication auth, ModelAndView model) {
        model.setViewName("upload");
        if (files.isEmpty()) {
            model.addObject("failure", true);
            return model;
        }
        Account account = accountRepository.findByUsername(auth.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        int i = 0;
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                model.addObject("failure", true);
                return model;
            }
            Snowflake id = fileStorageService.storeFile(file, account);
            DBFile dbFile = dbFileRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("File not found"));
            System.out.println(dbFile);
        }
        model.addObject("success", true);
        return model;
    }

    @GetMapping("/image/{id}")
    public String getImage(@PathVariable("id") String id) {
        return "redirect:/image/" + id + "/" + dbFileRepository.findById(Snowflake.of(id)).map(DBFile::getName).orElse(null);
    }

    @GetMapping("/image/{id}/{name}")
    @ResponseBody
    public ResponseEntity<?> getImage(@PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(id, name);
    }

    private ResponseEntity<?> getFile(String id, String name) {
        return getFile(null, id, name);
    }

    private ResponseEntity<?> getFile(String owner, String id, String name) {
        Snowflake snowflake = Snowflake.of(id);
        Optional<DBFile> file = dbFileRepository.findById(snowflake);
        if (!file.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        DBFile actualFile = file.get();
        if (name != null && !name.equals(actualFile.getName())) {
            return ResponseEntity.notFound().build();
        }
        if (owner != null) {
            Snowflake ownerSnowflake = Snowflake.of(owner);
            if (!actualFile.getOwner().getId().equals(ownerSnowflake)) {
                return ResponseEntity.notFound().build();
            }
        }
        if (actualFile.getData() == null || actualFile.getData().length == 0) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.get().getType()))
                .body(actualFile.getData());
    }

    @GetMapping("/attachment/{owner}/{id}/{name}")
    @ResponseBody
    public ResponseEntity<?> getAttachment(@PathVariable("owner") String owner, @PathVariable("id") String id, @PathVariable("name") String name) {
        return getFile(owner, id, name);
    }

    @GetMapping("/attachment/{id}")
    public String getAttachment(@PathVariable("id") String id) {
        DBFile file = dbFileRepository.findById(Snowflake.of(id)).orElseThrow(() -> new EntityNotFoundException("File not found"));
        Snowflake owner = file.getOwner().getId();
        return MessageFormat.format("redirect:/attachment/{0}/{1}/{2}", owner, id, file.getName());
    }

}
