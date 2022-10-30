package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.ReportComic;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.ReportComicRepository;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityNotFoundException;

@Controller
@RequiredArgsConstructor
public class ReportComicController {
    private final ComicRepository comicRepository;
    private final ReportComicRepository reportComicRepository;

    @PostMapping("/report/comic")
   public String reportComic(Snowflake id, String message, Authentication authentication){
        Account account = (Account) authentication.getPrincipal();
        Comic comic = comicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comic not found")) ;
        ReportComic reportComic = new ReportComic();
        reportComic.setReporter(account);
        reportComic.setMessage(message);
        reportComic.setComic(comic);
        reportComicRepository.save(reportComic);
        return "redirect:/comic/" +id;
    }
}
