package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yomikaze.persistence.entity.Genre;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.GenreRepository;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.form.GenreForm;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/genre")
@RequiredArgsConstructor
public class GenreController {

    private final GenreRepository genreRepository;
    private final ComicRepository comicRepository;

    @GetMapping("/manage")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.manage')")
    public String manage(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable, Model model) {
        Page<Genre> genres = genreRepository.findAll(pageable);
        model.addAttribute("genres", genres);
        return "views/comic/genre/manage";
    }

    @GetMapping("/create")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.create')")
    public String create(@ModelAttribute GenreForm genreForm, Model model) {
        model.addAttribute("action", "create");
        return "views/comic/genre/form";
    }

    @PostMapping("/create")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.create')")
    public String create(@ModelAttribute @Validated GenreForm genreForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", "create");
            return "views/comic/genre/form";
        }
        Genre genre = new Genre();
        genre.setName(genreForm.getName());
        genre.setDescription(genreForm.getDescription());
        genreRepository.save(genre);
        return "redirect:/genre/manage";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.edit')")
    public String edit(@PathVariable Snowflake id, @ModelAttribute GenreForm genreForm, Model model) {
        model.addAttribute("action", id + "/edit");
        Genre genre = genreRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        genreForm.setName(genre.getName());
        genreForm.setDescription(genre.getDescription());
        return "views/comic/genre/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.edit')")
    public String edit(@PathVariable Snowflake id, @ModelAttribute @Validated GenreForm genreForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", id + "/edit");
            return "views/comic/genre/form";
        }
        Genre genre = genreRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        genre.setName(genreForm.getName());
        genre.setDescription(genreForm.getDescription());
        genreRepository.save(genre);
        return "redirect:/genre/manage";
    }

    @GetMapping("/{id}/delete")
    @PreAuthorize("authentication != null && !anonymous")
    @PostAuthorize("hasAuthority('genre.delete')")
    public String delete(@PathVariable Snowflake id) {
        Genre genre = genreRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        genre.getComics().forEach(comic -> {
            comic.getGenres().remove(genre);
            comicRepository.save(comic);
        });
        genreRepository.delete(genre);
        return "redirect:/genre/manage";
    }

}
