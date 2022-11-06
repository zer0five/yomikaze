package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Request;
import org.yomikaze.persistence.entity.Role;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.persistence.repository.RequestRepository;
import org.yomikaze.persistence.repository.RoleRepository;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.form.RequestForm;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/request")
@PreAuthorize("authentication != null && !anonymous")
public class RequestController {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;

    //for user
    @PostAuthorize("hasAuthority('request.create.uploader')")
    @GetMapping
    public String request(@PageableDefault(size = Integer.MAX_VALUE) Pageable pageable, Model model, Authentication authentication, @ModelAttribute RequestForm requestForm) {
        Account account = (Account) authentication.getPrincipal();
        Pageable actualPageable = pageable.getPageSize() == Integer.MAX_VALUE ? Pageable.unpaged() : pageable;
        Page<Request> requests = requestRepository.findAllByRequester(account, actualPageable);
        model.addAttribute("requests", requests);
        return "/views/request/request-uploader";
    }

    @PostAuthorize("hasAuthority('request.create.uploader')")
    @PostMapping
    public String request(
        @Validated @ModelAttribute RequestForm requestForm,
        BindingResult bindingResult,
        Authentication authentication,
        @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable,
        Model model
    ) {
        Account account = (Account) authentication.getPrincipal();
        if (bindingResult.hasErrors()) {
            model.addAttribute("showForm", true);
            Pageable actualPageable = pageable.getPageSize() == Integer.MAX_VALUE ? Pageable.unpaged() : pageable;
            Page<Request> requests = requestRepository.findAllByRequester(account, actualPageable);
            model.addAttribute("requests", requests);
            return "/views/request/request-uploader";
        }
        Request request = new Request();
        request.setRequester(account);
        request.setMessage(requestForm.getMessage());
        requestRepository.save(request);
        return "redirect:/request";
    }



    //for admin
    @PostAuthorize("hasAuthority('request.cancel')")
    @GetMapping("/{id}/cancel")
    public String delete(@PathVariable("id") Snowflake id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Request not found!"));

        if (request.getRequester().equals(account)) {
            request.setApproved(false);
            request.setApprovedBy(account);
            requestRepository.save(request);
        }
        return "redirect:/request";
    }

    //for admin
    @PostAuthorize("hasAuthority('request.manage')")
    @GetMapping({"/manage"})
    public String requestAdmin(Pageable pageable, Model model) {
        Page<Request> requests = requestRepository.findAllByApprovedIsNull(pageable);
        model.addAttribute("requests", requests);
        return "views/request/request-page-admin";
    }

    @PostAuthorize("hasAuthority('request.manage')")
    @GetMapping({"/{id}/approve"})
    public String approve(@PathVariable("id") Snowflake id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Request not found!"));
        request.setApproved(true);
        request.setApprovedBy(account);
        requestRepository.save(request);
        Account requester = request.getRequester();
        Role uploader = roleRepository.findByName("Uploader").orElseThrow(() -> new EntityNotFoundException("Role not found!"));
        Set<Role>roles = new HashSet<>(requester.getRoles());
        roles.add(uploader);
        requester.setRoles(roles);
        accountRepository.save(requester);
        return "redirect:/request/manage";
    }

    @PostAuthorize("hasAuthority('request.manage')")
    @GetMapping({"/{id}/reject"})
    public String reject(@PathVariable("id") Snowflake id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Request request = requestRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Request not found!"));
        request.setApproved(false);
        request.setApprovedBy(account);
        requestRepository.save(request);
        return "redirect:/request/manage";
    }


}
