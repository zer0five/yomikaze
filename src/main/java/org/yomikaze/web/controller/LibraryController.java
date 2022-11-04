package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Library;
import org.yomikaze.service.LibraryService;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/library")
@PreAuthorize("authentication != null && !anonymous")
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping({"", "/"})
    public String library(Pageable pageable, Model model, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Page<Library> libraries = libraryService.getLibrary(account, pageable);
        model.addAttribute("libraries", libraries);
        return "views/library/index";
    }

    @GetMapping("/add/{id}")
    public ResponseEntity<Object> add(Authentication authentication, @PathVariable("id") Snowflake id) {
        Account account = (Account) authentication.getPrincipal();
        libraryService.addComic(account, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remove/{id}")
    public ResponseEntity<Object> remove(Authentication authentication, @PathVariable("id") Snowflake id) {
        Account account = (Account) authentication.getPrincipal();
        libraryService.removeComic(account, id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler({EntityExistsException.class})
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
