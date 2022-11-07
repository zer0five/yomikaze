package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.HistoryRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/history")
public class HistoryController {
    private final HistoryRepository historyRepository;

    @PreAuthorize("authentication != null && !anonymous")
    @GetMapping({"", "/"})
    public String listing(
        @PageableDefault(size = 12) Pageable pageable,
        Model model,
        Authentication authentication
    ) {
        Account account = (Account) authentication.getPrincipal();
        Pageable withSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Comic.DEFAULT_SORT);
        Collection<Comic> histories = historyRepository.findDistinctChapterComicByAccount(account);
        List<Comic> comicList = new ArrayList<>(histories);
        Page<Comic> comics = new PageImpl<>(comicList);
        model.addAttribute("comics", comics);
        return "views/history/history-list";
    }


}
