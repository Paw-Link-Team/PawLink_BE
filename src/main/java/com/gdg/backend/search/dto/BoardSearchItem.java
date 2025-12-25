package com.gdg.backend.search.dto;

public record BoardSearchItem(
        Long boardId,
        String title,
        String location,
        String walkTimeType
) {}
