package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.service.ComicService;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.comic.ComicDetailModel;
import org.yomikaze.web.dto.comic.ComicInputModel;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comic")
@Slf4j
@Validated
public class ComicController {

    private final ComicRepository comicRepository;
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
    public String search(Optional<String> q, Optional<String> cq, Optional<Integer> page, Optional<Integer> size, Model model) {
        int pageNumber = Math.max(1, page.orElse(1)) - 1;
        int pageSize = Math.max(12, size.orElse(12));
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        if (q.isPresent()) {
            model.addAttribute("comics", comicRepository.findByNameStartingWithIgnoreCase(q.get(), pageable));
        } else if (cq.isPresent()) {
            model.addAttribute("comics", comicRepository.findByNameContainingIgnoreCase(cq.get(), pageable));
        } else {
            model.addAttribute("comics", comicRepository.findAll(pageable));
        }
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

    @GetMapping("/chapter/create")
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.create')")
    public String createChapter(@ModelAttribute("comic") ComicInputModel comic) {
        return "views/comic/chapter/create";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.edit')")
    public String edit(@PathVariable long id, Model model) {
        Snowflake snowflake = Snowflake.of(id);
        Comic comic = comicRepository.findById(snowflake)
            .orElseThrow(() -> new EntityNotFoundException("comic.not-found"));
        model.addAttribute("comic", comic);
        return "views/comic/edit";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "redirect:/error/404";
    }

}
