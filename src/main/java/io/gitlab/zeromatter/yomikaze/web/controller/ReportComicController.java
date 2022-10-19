package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.ReportComic;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.ComicRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.ReportComicRepository;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.EntityNotFoundException;

@Controller
@RequiredArgsConstructor
public class ReportComicController {

    private final AccountRepository accountRepository;
    private final ComicRepository comicRepository;
    private final ReportComicRepository reportComicRepository;

    @PostMapping("/report/comic")
   public String reportComic(long id, String message, Authentication authentication){
        Account account = accountRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Comic comic = comicRepository.findById(Snowflake.of(id)).orElseThrow(() -> new EntityNotFoundException("Comic not found")) ;
        ReportComic reportComic = new ReportComic();
        reportComic.setReporter(account);
        reportComic.setMessage(message);
        reportComic.setComic(comic);
        reportComicRepository.save(reportComic);
        return "redirect:/comic/" +id;
    }
}
