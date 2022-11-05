package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.service.CommentService;
import org.yomikaze.snowflake.Snowflake;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comic/{comicId}/comment/post")
    public ResponseEntity<Object> postComment(@PathVariable("comicId") Snowflake comicId, String content, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        commentService.postComment(account, comicId, content);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comic/{comicId}/chapter/{index}/comment/post")
    public ResponseEntity<Object> postComment(@PathVariable("comicId") Snowflake comicId, @PathVariable("index") int index, String content, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        commentService.postComment(account, comicId, index, content);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{id}/edit")
    public ResponseEntity<Object> editComment(Authentication authentication, @PathVariable("id") Snowflake id, String content) {
        Account account = (Account) authentication.getPrincipal();
        commentService.editComment(account, id, content);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{id}/reply")
    public ResponseEntity<Object> replyComment(@PathVariable("id") Snowflake id, String content, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        commentService.replyComment(account, id, content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{id}/delete")
    public ResponseEntity<Object> deleteComment(@PathVariable("id") Snowflake id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        commentService.deleteComment(account, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comment/{id}/like/toggle")
    public ResponseEntity<Object> toggleLikeComment(@PathVariable("id") Snowflake id, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        commentService.toggleLikeComment(account, id);
        return ResponseEntity.ok().build();
    }


}
