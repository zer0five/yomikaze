package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.LibraryRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/library")
@PreAuthorize("authentication != null && isAuthenticated()")
public class LibraryController {

    private final LibraryRepository libraryRepository;

    @GetMapping({"", "/"})
    public String library(Optional<Integer> page, Optional<Integer> size, Model model, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        int pageNumber = Math.max(1, page.orElse(1)) - 1;
        int pageSize = Math.max(12, size.orElse(12));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        model.addAttribute("comics", libraryRepository.findAllByAccount(account, pageable));
        return "views/library/index";
    }
}
