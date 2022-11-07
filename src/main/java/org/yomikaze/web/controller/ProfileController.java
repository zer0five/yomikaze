package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Profile;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.persistence.repository.ProfileRepository;
import org.yomikaze.service.AuthenticationService;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.form.EditProfileForm;

import javax.persistence.EntityNotFoundException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")

public class ProfileController {

    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final AuthenticationService authenticationService;

    @GetMapping
    @PreAuthorize("authentication != null && !anonymous")
    public String profile(Authentication authentication, Model model) {
        Account account = (Account) authentication.getPrincipal();
        Profile profile = profileRepository.findById(account.getId()).orElseThrow(EntityNotFoundException::new);
        model.addAttribute("profile", profile);
        return "views/profile/profile-page";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable("id") Snowflake id, Model model) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Profile profile = account.getProfile();
        model.addAttribute("profile", profile);
        return "views/profile/profile-page";
    }

    @PreAuthorize("authentication != null && !anonymous")
    @GetMapping("/{id}/edit")

    public String editProfile(@PathVariable("id") Snowflake id, @ModelAttribute EditProfileForm editProfileForm, Authentication authentication, Model model) {
        Account findAccount = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Account account = (Account) authentication.getPrincipal();
        if (!account.getId().equals(findAccount.getId())) {
            throw new AccessDeniedException("");
        }
        //profile service
        Profile profile = findAccount.getProfile();
        model.addAttribute("profile", profile);
        editProfileForm.setDisplayName(profile.getDisplayName());
        editProfileForm.setBirthday(profile.getBirthday());
        editProfileForm.setBio(profile.getBio());
        editProfileForm.setShowEmail(profile.isShowEmail());
        editProfileForm.setShowLibrary(profile.isShowLibrary());
        editProfileForm.setShowBirthday(profile.isShowBirthday());


        return "views/profile/profile-edit";
    }

    @PostMapping("/{id}/edit")
    public String editProfile(@PathVariable("id") Snowflake id, @ModelAttribute @Validated EditProfileForm editProfileForm,
                              BindingResult bindingResult, Authentication authentication) {
        Account accountRepo = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Account account = (Account) authentication.getPrincipal();
        Profile profile = accountRepo.getProfile();
        if (bindingResult.hasErrors()) {
            return "redirect:/profile/{id}/edit";
        }

        if (!account.getId().equals(accountRepo.getId())) {
            throw new AccessDeniedException("");
        }
        profile.setDisplayName(editProfileForm.getDisplayName());
        profile.setBirthday(editProfileForm.getBirthday());
        profile.setBio(editProfileForm.getBio());
        profile.setShowLibrary(editProfileForm.isShowLibrary());
        profile.setShowEmail(editProfileForm.isShowEmail());
        profile.setShowBirthday(editProfileForm.isShowBirthday());

        account.setProfile(profile);
        account = accountRepository.save(account);
        authenticationService.refreshAuthentication(account);
        return "redirect:/profile";

    }
}
