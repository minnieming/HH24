package com.sparta.spring_01.dto;

import com.sparta.spring_01.entity.Post;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostResponseDto { // 클라이언트에게 반환되는 게시글 정보에 비밀번호는 제외시키려고 비밀번호 필드를 삭제함.
    private Long id;
    private String title;
    private String username;
    private String content;
    private LocalDate date;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.username = post.getUsername();
        this.content = post.getContent();
        this.date = post.getDate();
    }

    public PostResponseDto(Long id, String title, String username, String content, LocalDate date) {
        this.id = id;
        this.title = title;
        this.username = username;
        this.content = content;
        this.date = date;
    }
}
