package com.sparta.reviewservice.domain.service;

import com.sparta.reviewservice.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "[ReviewService]")
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
}
