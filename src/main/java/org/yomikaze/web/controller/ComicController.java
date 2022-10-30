package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.ComicDetailModel;
import org.yomikaze.web.dto.comic.ComicInputModel;
import org.yomikaze.web.dto.comic.chapter.ChapterInputModel;

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

    @GetMapping({"", "/"})
    public String comic() {
        return "redirect:/comic/listing";
    }

    @RequestMapping("/listing")
    public String listing(@PageableDefault(size = 12) Pageable pageable,
                          Model model) {
        Pageable withSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Comic.DEFAULT_SORT);
        Page<Comic> comics = comicRepository.findAll(withSort);
        model.addAttribute("comics", comics);
        return "views/comic/listing";
    }

    @GetMapping({"/detail/{id}", "detail/{id}/{name}"})
    public String detail(@PathVariable Snowflake id, @PathVariable Optional<String> name, Model model) {
        Comic comic = comicRepository.findById(id).orElse(null);
        if (comic == null) {
            model.addAttribute("error", "comic.not-found");
            return "forward:/error/404";
        }
        String slug = comicService.getSlug(comic);
        if (!name.map(slug::equals).orElse(true)) {
            return MessageFormat.format("redirect:/comic/detail/{0}/{1}", String.valueOf(id), slug);
        }
        ComicDetailModel detailModel = comicService.getComicDetail(comic);
        model.addAttribute("comic", detailModel);
        return "views/comic/detail";
    }

    @GetMapping("/search")
    public String search(
        @PageableDefault(size = 12) Pageable pageable,
        Model model,
        Optional<Long> genre,
        Optional<String> q, Optional<String> cq
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
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.create')")
    public String create(@ModelAttribute("comic") ComicInputModel comic) {
        return "views/comic/create";
    }

    @PostMapping(path = "/create", consumes = {"multipart/form-data"})
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.create')")
    public String create(@RequestPart MultipartFile thumbnail, @Validated @ModelAttribute("comic") ComicInputModel comic, BindingResult bindingResult, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "views/comic/create";
        }
        Account uploader = (Account) authentication.getPrincipal();
        Comic saved = comicService.createComic(comic, thumbnail, uploader);
        return MessageFormat.format("redirect:/comic/detail/{0}/{1}", saved.getId(), comicService.getSlug(saved));
    }

    @GetMapping("/{id}/chapter/add")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('chapter.add')")
    public String createChapter(@ModelAttribute("chapter") ChapterInputModel chapter, @PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("comic", comicService.getComicDetail(comic));
        return "views/comic/chapter/add";
    }

    @PostMapping("/{id}/chapter/add")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('chapter.add')")
    public String createChapter(@ModelAttribute("chapter") ChapterInputModel chapter, @ModelAttribute("comic") ComicDetailModel comic, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "views/comic/chapter/add";
        }
        comicService.addChapter(comic, chapter);
        return MessageFormat.format("redirect:/comic/detail/{0}", comic.getId());
    }

    @GetMapping("/{id}/chapter/{index}")
    public String readChapter(@PathVariable Snowflake id, @PathVariable int index, Model model) {
        Comic comic = comicRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        List<Chapter> chapterList = new ArrayList<>(comic.getChapters());
        if (index < 0 || index >= comic.getChapters().size()) {
            return "forward:/error/404";
        }
        model.addAttribute("comic", comicService.getComicDetail(comic));
        PageImpl<Chapter> chapters = new PageImpl<>(chapterList, PageRequest.of(index, 1), chapterList.size());
        Chapter chapter = chapterList.get(index);
        model.addAttribute("index", index);
        model.addAttribute("chapters", chapters);
        model.addAttribute("chapter", chapter);
        return "views/comic/chapter/read";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.edit')")
    public String edit(@PathVariable Snowflake id, Model model) {
        Comic comic = comicRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("comic.not-found"));
        model.addAttribute("comic", comic);
        return "views/comic/edit";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("exception", e);
        model.addAttribute("message", "comic.not-found");
        return "forward:/error/404";
    }

}
