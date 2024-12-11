package com.sparta.reviewservice.web.controller;

import com.sparta.reviewservice.domain.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j(topic = "[ReviewController]")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
}
