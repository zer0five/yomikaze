package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Profile;
import org.yomikaze.persistence.repository.ProfileRepository;
import org.yomikaze.web.dto.form.EditProfileForm;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")

public class ProfileController {
    private final ProfileRepository profileRepository;
    @GetMapping({"", "/"})

    public String profile(Authentication authentication, Model model) {
        Account account = (Account) authentication.getPrincipal();
        Profile profile = account.getProfile();
        model.addAttribute("profile", profile);


        return "/views/profile/profile-page";

    }
    @PreAuthorize("authentication != null && !anonymous")

    @GetMapping("/edit")

    public String editProfile(@ModelAttribute EditProfileForm  editProfileForm, Authentication authentication, Model model) {
        Account account = (Account) authentication.getPrincipal();
        Profile profile = account.getProfile();
        model.addAttribute("profile",profile);
        editProfileForm.setDisplayName(profile.getDisplayName());
        editProfileForm.setBirthday(profile.getBirthday());



        return "/views/profile/profile-edit";
    }
    @PostMapping("/edit")
    public String editProfile(@ModelAttribute @Validated  EditProfileForm editProfileForm, Authentication authentication){


        return "redirect:/edit";

    }
}
