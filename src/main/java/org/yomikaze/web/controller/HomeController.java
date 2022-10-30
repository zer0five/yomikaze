package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.yomikaze.persistence.entity.Comic;
import org.yomikaze.persistence.repository.ComicRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ComicRepository comicRepository;

    @GetMapping({"/", "/home", "/index"})
    public String home(Model model, Optional<Integer> page, Optional<Integer> size, HttpServletRequest request) {
        if (page.isPresent() || size.isPresent()) return "redirect:/comic/listing?" + request.getQueryString();
        Page<Comic> comics = comicRepository.findAll(PageRequest.of(0, 12, Comic.DEFAULT_SORT));
        model.addAttribute("comics", comics);
        return "views/home/index";
    }
}
