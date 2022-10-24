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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.service.ComicService;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.ComicDto;

import javax.persistence.EntityNotFoundException;
import java.text.MessageFormat;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comic")
@Slf4j
public class ComicController {

    private final ComicRepository comicRepository;
    private final ComicService comicService;

    private static final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 12, Sort.by("id").descending());

    @GetMapping({"", "/"})
    public String comic() {
        return "redirect:/comic/listing";
    }

    @GetMapping("/listing")
    public String listing(@PageableDefault(page = 1, size = 12, sort = {"updatedAt", "id"}, direction = Sort.Direction.DESC) Pageable pageable,
                          Model model) {
        Pageable realPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        log.info("Listing comics with pageable {}", realPageable);
        Page<Comic> comics = comicRepository.findAll(realPageable);
        model.addAttribute("comics", comics);
        return "views/comic/listing";
    }

    @GetMapping({"/detail/{id}", "/detail/{name}.{id}"})
    public String detail(@PathVariable Optional<String> name, @PathVariable long id, Model model) {
        Snowflake snowflake = Snowflake.of(id);
        Comic comic = comicRepository.findById(snowflake)
            .orElseThrow(() -> new EntityNotFoundException("comic.not-found"));
        String slug = comicService.getSlug(comic);
        if (name.map(slug::equals).orElse(false)) {
            return MessageFormat.format("redirect:/comic/detail/{0}.{1}", slug, id);
        }
        model.addAttribute("comic", comic);
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
    public String create(@ModelAttribute("comic") ComicDto comic) {
        return "views/comic/create";
    }

    @GetMapping("/chapter/create")
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.create')")
    public String createChapter(@ModelAttribute("comic") ComicDto comic) {
        return "views/comic/chapter/create";
    }

    @PostMapping("/create")
    @PreAuthorize("authentication != null && authenticated")
    @PostAuthorize("hasAuthority('comic.create')")
    public String create(@ModelAttribute Comic comic, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/comic/create";
        }
        comicRepository.save(comic);
        return "redirect:/comic/listing";
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
