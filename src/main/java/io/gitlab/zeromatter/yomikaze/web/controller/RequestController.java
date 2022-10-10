package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Request;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.RequestRepository;
import io.gitlab.zeromatter.yomikaze.web.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RequestController {
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;


    @GetMapping("/request")
    public String request() {
        return "request";

    }

    @PostMapping("/request")
    public ResponseEntity<Object> request(String message, Authentication authentication) {
            Account account = accountRepository.findByUsername(authentication.getName()).orElseThrow(()-> new UsernameNotFoundException("User not found!")) ;
            Request request = new Request();
            request.setRequester(account);
            request.setMessage(message);
            requestRepository.save(request);

            return ResponseEntity.ok(GenericResponse.builder().status(true).message("Thanh cong"));
    }


}
