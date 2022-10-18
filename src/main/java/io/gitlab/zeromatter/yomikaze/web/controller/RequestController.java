package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Request;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.RequestRepository;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;

    //for user
    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAuthority('request.create.uploader')")
    @GetMapping({"", "/"})
    public ModelAndView request(ModelAndView modelAndView, Authentication authentication, Optional<Integer> page, Optional<Integer> size) {
        Account account = accountRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        modelAndView.setViewName("request-page");
        Pageable pageable = PageRequest.of(page.orElse(1) - 1, 5);
        Page<Request> requests = requestRepository.findAllByRequester(account, pageable);
        modelAndView.addObject("requests", requests);
        return modelAndView;

    }

    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAuthority('request.create.uploader')")
    @PostMapping({"", "/"})
    public String request(String message, Authentication authentication) {
        Account account = accountRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Request request = new Request();
        request.setRequester(account);
        request.setMessage(message);
        requestRepository.save(request);

        return "redirect:/request";
    }

    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAuthority('request.cancel')")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id, Authentication authentication) {
        Account account = accountRepository.findByUsername(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Request request = requestRepository.findById(Snowflake.of(id)).orElseThrow(() -> new EntityNotFoundException("Request not found!"));

        if (request.getRequester().equals(account)) {
            requestRepository.delete(request);
        }
        return "redirect:/request";
    }

    //for admin
    @PreAuthorize("authentication != null && isAuthenticated()")
    @PostAuthorize("hasAuthority('request.create.uploader')")
    @GetMapping({"/admin"})
    public ModelAndView requestAdmin(ModelAndView modelAndView, Authentication authentication, Optional<Integer> page, Optional<Integer> size) {

        modelAndView.setViewName("request-page-admin");
        Pageable pageable = PageRequest.of(page.orElse(1) - 1, 5);
        Page<Request> requests = requestRepository.findAllByApprovedIsNull(pageable);
        modelAndView.addObject("requests", requests);
        return modelAndView;

    }


}
