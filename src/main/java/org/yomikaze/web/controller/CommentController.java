package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.entity.Comment;
import org.yomikaze.service.CommentService;
import org.yomikaze.snowflake.Snowflake;

import java.text.MessageFormat;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    // comic
    @PostMapping("/comic/{comicId}/comment/post")
    public String postComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        String content
    ) {
        Account account = (Account) authentication.getPrincipal();
        Comment comment = commentService.postComment(account, comicId, content);
        return MessageFormat.format("redirect:/comic/{0}/detail#comment-{1}", comicId, comment.getId());
    }

    // chapter
    @PostMapping("/comic/{comicId}/chapter/{index}/comment/post")
    public String postComment(@PathVariable("comicId") Snowflake comicId, @PathVariable("index") int index, String content, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        Comment comment = commentService.postComment(account, comicId, index, content);
        return MessageFormat.format("redirect:/comic/{0}/chapter/{1}#comment-{2}", comicId, index, comment.getId());
    }

    // comic
    @PostMapping("/comic/{comicId}/comment/{commentId}/reply")
    public String postComment(
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("commentId") Snowflake commentId,
        String content, Authentication authentication
    ) {
        Account account = (Account) authentication.getPrincipal();
        Comment comment = commentService.replyComment(account, commentId, content);
        return MessageFormat.format("redirect:/comic/{0}/detail#comment-{1}", comicId, comment.getId());
    }

    // chapter
    @PostMapping("/comic/{comicId}/chapter/{index}/comment/{commentId}/reply")
    public String replyComment(
        Authentication authentication,
        @PathVariable("commentId") Snowflake id,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("index") int index,
        String content
    ) {
        Account account = (Account) authentication.getPrincipal();
        Comment comment = commentService.replyComment(account, id, content);
        return MessageFormat.format("redirect:/comic/{0}/chapter/{1}#comment-{2}", comicId, index, comment.getId());
    }

    // comic
    @PostMapping("/comic/{comicId}/comment/{commentId}/edit")
    public String editComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("commentId") Snowflake commentId,
        String content
    ) {
        Account account = (Account) authentication.getPrincipal();
        commentService.editComment(account, commentId, content);
        return MessageFormat.format("redirect:/comic/{0}/detail#comment-{1}", comicId, commentId);
    }

    // chapter
    @PostMapping("/comic/{comicId}/chapter/{index}/comment/{id}/edit")
    public String editComment(
        Authentication authentication,
        @PathVariable("id") Snowflake id,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("index") int index,
        String content
    ) {
        Account account = (Account) authentication.getPrincipal();
        Comment comment = commentService.editComment(account, id, content);
        return MessageFormat.format("redirect:/comic/{0}/chapter/{1}#comment-{2}", comicId, index, comment.getId());
    }

    // comic
    @PostMapping("/comic/{comicId}/comment/{commentId}/delete")
    public String deleteComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("commentId") Snowflake commentId
    ) {
        Account account = (Account) authentication.getPrincipal();
        commentService.deleteComment(account, commentId);
        return MessageFormat.format("redirect:/comic/{0}/detail", comicId);
    }

    // chapter
    @PostMapping("/comic/{comicId}/chapter/{index}/comment/{commentId}/delete")
    public String deleteComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("index") int index,
        @PathVariable("commentId") Snowflake commentId
    ) {
        Account account = (Account) authentication.getPrincipal();
        commentService.deleteComment(account, commentId);
        return MessageFormat.format("redirect:/comic/{0}/chapter/{1}", comicId, index);
    }

    // comic
    @GetMapping("/comic/{comicId}/comment/{commentId}/like")
    public String likeComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("commentId") Snowflake commentId
    ) {
        Account account = (Account) authentication.getPrincipal();
        commentService.toggleLikeComment(account, commentId);
        return MessageFormat.format("redirect:/comic/{0}/detail#comment-{1}", comicId, commentId);
    }

    // chapter
    @GetMapping("/comic/{comicId}/chapter/{index}/comment/{commentId}/like")
    public String likeComment(
        Authentication authentication,
        @PathVariable("comicId") Snowflake comicId,
        @PathVariable("index") int index,
        @PathVariable("commentId") Snowflake commentId
    ) {
        Account account = (Account) authentication.getPrincipal();
        commentService.toggleLikeComment(account, commentId);
        return MessageFormat.format("redirect:/comic/{0}/chapter/{1}#comment-{2}", comicId, index, commentId);
    }


}
