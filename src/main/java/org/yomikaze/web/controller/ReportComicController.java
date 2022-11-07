package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.ReportComic;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.ReportComicRepository;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;

@Controller
@RequiredArgsConstructor
@PreAuthorize("authentication != null && !anonymous")
public class ReportComicController {
    private final ComicRepository comicRepository;
    private final ReportComicRepository reportComicRepository;

    @PostMapping("/comic/{id}/report")
    public String reportComic(@PathVariable Snowflake id, String message, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ReportComic reportComic = new ReportComic();
        reportComic.setReporter(account);
        reportComic.setMessage(message);
        reportComic.setComic(comic);
        reportComicRepository.save(reportComic);
        return MessageFormat.format("redirect:/comic/{0}/detail", id);
    }

    @GetMapping("/report/manage")
    @PostAuthorize("hasAuthority('report.manage')")
    public String manageReport(Model model) {
        model.addAttribute("reports", reportComicRepository.findAllByApprovedIsNull());
        return "views/report/manage";
    }
}
