package com.sparta.spring_01.dto;

import com.sparta.spring_01.entity.Post;
import lombok.Getter;

@Getter
public class PostResponseDto {
    private Long id;
    private String book;
    private String writer;
    private int price;

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.book = post.getBook();
        this.writer = post.getWriter();
        this.price = post.getPrice();
    }

    public PostResponseDto(Long id, String book, String writer, int price) {
        this.id = id;
        this.book = book;
        this.writer = writer;
        this.price = price;
    }
}
