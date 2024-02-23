package com.sparta.spring_01.entity;

import com.sparta.spring_01.dto.PostRequestDto;
import com.sparta.spring_01.dto.PostResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class Post {
    private Long id;
    private String title;
    private String username;
    private String password;
    private String content;
    private LocalDate date; // 날짜를 쉽게 다루고, 날짜 관련 연산이나 형식화도 간단하게 처리 가능.

    public Post(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.content = requestDto.getContent();
        this.date = requestDto.getDate();
    }

    public void update(PostRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.content = requestDto.getContent();
        this.date = requestDto.getDate();
    }

    public PostResponseDto toResponseDto() {

        return new PostResponseDto(this.id, this.title, this.username, this.content, this.date);

    }
}
