package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.*;
import org.yomikaze.persistence.repository.ComicRepository;
import org.yomikaze.persistence.repository.CommentRepository;
import org.yomikaze.persistence.repository.PermissionRepository;
import org.yomikaze.snowflake.Snowflake;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PermissionRepository permissionRepository;
    private final ComicRepository comicRepository;

    public void postComment(Account account, Comic comic, String content) {
        Comment comment = new Comment();
        comment.setAccount(account);
        comment.setComic(comic);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public void postComment(Account account, Snowflake comicId, String content) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(EntityNotFoundException::new);
        postComment(account, comic, content);
    }

    public void postComment(Account account, Chapter chapter, String content) {
        Comment comment = new Comment();
        comment.setAccount(account);
        comment.setComic(chapter.getComic());
        comment.setChapter(chapter);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public void postComment(Account account, Snowflake comicId, int index, String content) {
        Comic comic = comicRepository.findById(comicId).orElseThrow(EntityNotFoundException::new);
        Chapter chapter = comic.getChapters().get(index);
        postComment(account, chapter, content);
    }

    public void replyComment(Account account, Comment parent, String content) {
        Comment comment = new Comment();
        comment.setAccount(account);
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        comment.setParent(parent);
        comment.setContent(content);
        parent.getReplies().add(comment);
        commentRepository.save(parent);
    }

    public void replyComment(Account account, Snowflake id, String content) {
        Comment parent = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        replyComment(account, parent, content);
    }

    public void likeComment(Account account, Comment comment) {
        comment.getLikedBy().add(account);
        commentRepository.save(comment);
    }

    public void unlikeComment(Account account, Comment comment) {
        comment.getLikedBy().remove(account);
        commentRepository.save(comment);
    }

    public Page<Comment> getComments(Comic comic, Pageable pageable) {
        return commentRepository.findAllByComicId(comic.getId(), pageable);
    }

    public List<Comment> getComments(Comic comic) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.sort(Comment.class).by(Comment::getId).descending());
        return commentRepository.findAllByComicId(comic.getId(), pageable).getContent();
    }

    public Page<Comment> getComments(Chapter chapter, Pageable pageable) {
        return commentRepository.findAllByChapterId(chapter.getId(), pageable);
    }

    public void editComment(Account account, Snowflake id, String content) {
        Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!account.equals(comment.getAccount())) {
            throw new AccessDeniedException("");
        }
        Permission editOther = permissionRepository.findByAuthority("comment.edit.other").orElseThrow(EntityNotFoundException::new);
        if (!account.getAuthorities().contains(editOther)) {
            throw new AccessDeniedException("");
        }
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public void deleteComment(Account account, Comment comment) {
        if (!account.equals(comment.getAccount())) {
            throw new AccessDeniedException("");
        }
        Permission deleteOther = permissionRepository.findByAuthority("comment.delete.other").orElseThrow(EntityNotFoundException::new);
        if (!account.getAuthorities().contains(deleteOther)) {
            throw new AccessDeniedException("");
        }
        commentRepository.delete(comment);
    }

    public void deleteComment(Account account, Snowflake id) {
        Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        deleteComment(account, comment);
    }

    public void toggleLikeComment(Account account, Snowflake id) {
        Comment comment = commentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (comment.getLikedBy().contains(account)) {
            unlikeComment(account, comment);
        } else {
            likeComment(account, comment);
        }
    }

    public boolean isLiked(Account account, Comment comment) {
        return comment.getLikedBy().contains(account);
    }
}
