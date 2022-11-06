package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Chapter;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.GenreRepository;
import org.yomikaze.service.ComicService;
import org.yomikaze.service.HistoryService;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.ComicDetailModel;
import org.yomikaze.web.dto.comic.CreateComicForm;
import org.yomikaze.web.dto.comic.chapter.ChapterForm;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comic")
@Slf4j
@Validated
public class ComicController {

    private final ComicRepository comicRepository;
    private final GenreRepository genreRepository;
    private final ComicService comicService;
    private final HistoryService historyService;

    @GetMapping({"", "/"})
    public String comic() {
        return "redirect:/comic/listing";
    }

    @RequestMapping("/listing")
    public String listing(
        @PageableDefault(size = 12) Pageable pageable,
        Model model
    ) {
        Pageable withSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Comic.DEFAULT_SORT);
        Page<Comic> comics = comicRepository.findAll(withSort);
        model.addAttribute("comics", comics);
        return "views/comic/listing";
    }

    @GetMapping("{id}/detail")
    public String detail(@PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ComicDetailModel detailModel = comicService.getComicDetail(comic);
        model.addAttribute("comic", detailModel);
        model.addAttribute("chapters", comic.getChapters());
        return "views/comic/detail";
    }

    @GetMapping("/search")
    public String search(
        @PageableDefault(size = 12) Pageable pageable,
        Model model,
        Optional<Long> genre,
        Optional<String> q,
        Optional<String> cq
    ) {
        Pageable withSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Comic.DEFAULT_SORT);
        Page<Comic> result = new PageImpl<>(Collections.emptyList());
        if (genre.isPresent()) {
            Genre genreEntity = genreRepository.findById(Snowflake.of(genre.get())).orElse(null);
            if (genreEntity != null) {
                result = comicRepository.findByGenresContaining(genreEntity, withSort);
            }
            model.addAttribute("genre", genreEntity);
        } else if (q.isPresent()) {
            result = comicRepository.findByNameStartingWithIgnoreCase(q.get(), withSort);
            model.addAttribute("search", q.get());
        } else if (cq.isPresent()) {
            result = comicRepository.findByNameContainingIgnoreCase(cq.get(), withSort);
            model.addAttribute("search", cq.get());
        } else {
            result = comicRepository.findAll(withSort);
        }
        model.addAttribute("comics", result);
        return "views/comic/search";
    }

    @GetMapping("/create")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.create')")
    public String create(@ModelAttribute("comic") CreateComicForm comic) {
        return "views/comic/form";
    }

    @PostMapping(path = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.create')")
    public String create(@RequestPart MultipartFile thumbnail, @Validated @ModelAttribute("comic") CreateComicForm comic, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "views/comic/form";
        }
        Account uploader = (Account) authentication.getPrincipal();
        Comic saved = comicService.createComic(comic, thumbnail, uploader);
        return MessageFormat.format("redirect:/comic/{0}/detail", saved.getId());
    }

    @GetMapping("/{id}/chapter/add")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('chapter.add')")
    public String createChapter(@ModelAttribute("chapter") ChapterForm chapter, @PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        chapter.setIndex(comic.getChapters().size());
        chapter.setTitle("Chapter " + (comic.getChapters().size() + 1));
        model.addAttribute("comic", comic);
        return "views/comic/chapter/add";
    }

    @PostMapping("/{id}/chapter/add")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('chapter.add')")
    public String createChapter(@ModelAttribute("chapter") ChapterForm chapter, @ModelAttribute("comic") ComicDetailModel comic, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "views/comic/chapter/add";
        }
        comicService.addChapter(comic, chapter);
        return MessageFormat.format("redirect:/comic/{0}/detail", comic.getId());
    }

    @GetMapping("/{id}/chapter/{index}")
    public String readChapter(@PathVariable Snowflake id, @PathVariable int index, Model model, Authentication authentication) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Chapter> chapterList = new ArrayList<>(comic.getChapters());
        if (index < 0 || index >= comic.getChapters().size()) {
            return "forward:/error/404";
        }
        model.addAttribute("comic", comicService.getComicDetail(comic));
        boolean hasPrev = index > 0;
        boolean hasNext = index < chapterList.size() - 1;
        Chapter chapter = chapterList.get(index);
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
            Account account = (Account) authentication.getPrincipal();
            historyService.read(account, chapter);
        }
        model.addAttribute("index", index);
        model.addAttribute("chapters", chapterList);
        model.addAttribute("chapter", chapter);
        model.addAttribute("hasPrev", hasPrev);
        model.addAttribute("hasNext", hasNext);
        return "views/comic/chapter/read";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.edit')")
    public String edit(@PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("comic.not-found"));
        model.addAttribute("comic", comic);
        return "views/comic/edit";
    }

    @PostMapping(path = "/{id}/edit", consumes = {"multipart/form-data"})
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.edit')")
    public String edit(@PathVariable Snowflake id, @RequestPart MultipartFile thumbnail, @Validated @ModelAttribute("comic") CreateComicForm comic, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "views/comic/edit";
        }
        Account uploader = (Account) authentication.getPrincipal();
        Comic db = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!db.getUploader().equals(uploader)) {
            throw new AccessDeniedException("comic.edit.access-denied");
        }
        Comic saved = comicService.updateComic(id, comic, thumbnail);
        return MessageFormat.format("redirect:/comic/{0}/detail", saved.getId());
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.delete')")
    public String delete(@PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("comic.not-found"));
        model.addAttribute("comic", comic);
        return "views/comic/delete";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.delete')")
    public String delete(@PathVariable Snowflake id, Authentication authentication) {
        Account uploader = (Account) authentication.getPrincipal();
        Comic db = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!db.getUploader().equals(uploader)) {
            throw new AccessDeniedException("comic.delete.access-denied");
        }
        comicRepository.deleteById(id);
        return "redirect:/comic";
    }

    @GetMapping("/manage")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('comic.manage')")
    public String manage(@PageableDefault(size = 12) Pageable pageable,
                         Authentication authentication, Model model) {
        Account uploader = (Account) authentication.getPrincipal();
        Page<Comic> comics = comicRepository.findByUploader(uploader, pageable);
        model.addAttribute("comics", comics);
        return "views/comic/manage";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("exception", e);
        model.addAttribute("message", "comic.not-found");
        return "forward:/error/404";
    }

}
