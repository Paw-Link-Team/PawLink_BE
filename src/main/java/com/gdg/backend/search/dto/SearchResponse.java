package com.gdg.backend.search.dto;

import java.util.List;

public record SearchResponse(
        String keyword,
        List<BoardSearchItem> boards
) {}
