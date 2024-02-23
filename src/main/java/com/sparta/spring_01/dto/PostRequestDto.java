package com.sparta.spring_01.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostRequestDto {
    private String title;
    private String username;
    private String password;
    private String content;
    private LocalDate date;
}
