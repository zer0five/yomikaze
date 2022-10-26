package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Library;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.LibraryRepository;
import org.yomikaze.snowflake.Snowflake;

import java.net.URI;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/library")
@PreAuthorize("authentication != null && isAuthenticated()")
public class LibraryController {

    private final LibraryRepository libraryRepository;
    private final ComicRepository comicRepository;

    @GetMapping({"", "/"})
    public String library(Optional<Integer> page, Optional<Integer> size, Model model, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        int pageNumber = Math.max(1, page.orElse(1)) - 1;
        int pageSize = Math.max(12, size.orElse(12));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        model.addAttribute("comics", libraryRepository.findAllByAccount(account, pageable));
        return "views/library/index";
    }

    @GetMapping("/add/{id}")
    public ResponseEntity<Object> add(Authentication authentication, @PathVariable("id") Snowflake id) {
        Account account = (Account) authentication.getPrincipal();
        Optional<Comic> comic = comicRepository.findById(id);
        if (!comic.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Library> library = libraryRepository.findByAccountAndComic(account, comic.get());
        if (library.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Library newLibrary = new Library();
        newLibrary.setAccount(account);
        newLibrary.setComic(comic.get());
        libraryRepository.save(newLibrary);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remove/{id}")
    public ResponseEntity<Object> remove(Authentication authentication, @PathVariable("id") Snowflake id){
        Account account = (Account) authentication.getPrincipal();
        Optional<Library> library = libraryRepository.findByAccountAndComic_Id(account, id);
        if(!library.isPresent()){
            return ResponseEntity.notFound().build();
        }
        libraryRepository.delete(library.get());
        return ResponseEntity.ok().build();
    }

}
