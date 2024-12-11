package com.sparta.reviewservice.domain.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j(topic = "[DummyS3Util]")
public class DummyS3Util extends S3Util {

    //S3 더미 구현체
    @Override
    public String uploadImage(MultipartFile image) {
        // 이미지 파일 검증 이상 있으면 Exception 발생
        validateFile(image);
        validateExtension(image.getOriginalFilename());

        // S3에 저장할 파일 이름 생성
        String S3FileName = generateServerFileName(image.getOriginalFilename());
        log.info("파일 이름 업로드 완료.. 더미 구현체 {}", S3FileName);
        return "https://reviewservice.s3.ap-northeast-2.amazonaws.com/" + S3FileName;
    }
}
