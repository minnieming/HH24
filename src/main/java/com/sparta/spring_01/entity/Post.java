package com.sparta.spring_01.entity;

import com.sparta.spring_01.dto.PostRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    private Long id;
    private String book;
    private String writer;
    private int price;

    public Post(PostRequestDto requestDto) {
        this.book = requestDto.getBook();
        this.writer = requestDto.getWriter();
        this.price = requestDto.getPrice();
    }

    public void update(PostRequestDto requestDto) {
        this.book = requestDto.getBook();
        this.writer = requestDto.getWriter();
        this.price = requestDto.getPrice();
    }
}
