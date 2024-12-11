package com.sparta.reviewservice.domain.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j(topic = "[S3Util]")
public abstract class S3Util {

    // 1. 이미지 null인지, originalName 유무 검사
    // 2. 확장자 존재하는지, 지원하는 확장지인지 검사 사진 이니깐 (jpg, jpeg, png, webp)만 지원.
    // 3. S3에 저장할 파일 이름으로 바꾸자. DB에는 서버에 저장할 이름, 원래 이름으로 저장

    // 3. S3 업로드 => 추상 메서드

    // MultipartFile 받고 null인지 originalFileName이 존재하는지 검사
    protected void validateFile(MultipartFile image){
        image.getOriginalFilename();
        // MultipartFile은 파일 이름 없으면 빈 문자열 반환
        if(image.isEmpty() || image.getOriginalFilename().isBlank()){
            log.error("이미지 파일이 비어있습니다. {}", image);
            throw new IllegalArgumentException("이미지 파일이 비어있습니다.");

        }
    }

    // 확장자 존재 여부, 지원하는 확장자 인지 검사 (jpg, jpeg, png, webp)
    protected void validateExtension(String originalFileName){
        int pos = originalFileName.lastIndexOf(".");
        if(pos == -1) {
            log.error("확장자가 없습니다. {}", originalFileName);
            throw new IllegalArgumentException("확장자가 없습니다.");
        }

        String extension = originalFileName.substring(pos + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "jpeg", "png", "webp");
        if(!allowedExtensionList.contains(extension))  {
            log.error("지원하지 않는 확장자입니다. {}", extension);
            throw new IllegalArgumentException("지원하지 않는 확장자입니다.");
        }
    }

    // 서버에 같은 이름으로 이미지 저장되는거 막기위해서 서버에 저장될 이름을 따로 만들자.
    protected String generateServerFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1)
                .toLowerCase();

        String randomName = UUID.randomUUID().toString();

        return randomName + "." + extension;
    }

    // 추상 메서드로 더미 구현체 만들자
    public abstract String uploadImage(MultipartFile image);
}
