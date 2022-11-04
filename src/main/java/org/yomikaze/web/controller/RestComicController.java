package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.ComicRepository;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/v1/comic")
@RestController
@RequiredArgsConstructor
public class RestComicController {

    private final ComicRepository comicRepository;

    @GetMapping("/manage")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.manage')")
    public ResponseEntity<Object> manage(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Pageable actualPageable = pageable.getPageSize() == Integer.MAX_VALUE ? Pageable.unpaged() : pageable;
        Page<Comic> comics = comicRepository.findByUploader(account, actualPageable);
        if (comics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("page", comics.getNumber());
        response.put("totalPages", comics.getTotalPages());
        response.put("totalElements", comics.getTotalElements());
        response.put("comics", comics.getContent());
        return ResponseEntity.ok(response);
    }

}
