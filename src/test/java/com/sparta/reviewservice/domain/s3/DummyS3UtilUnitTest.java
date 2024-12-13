package com.sparta.reviewservice.domain.s3;

import com.sparta.reviewservice.domain.exception.S3Exception;
import com.sparta.reviewservice.domain.s3.DummyS3Util;
import com.sparta.reviewservice.domain.s3.S3Util;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DummyS3UtilUnitTest {

    private final S3Util s3Util = new DummyS3Util();

    @Test
    @DisplayName("유효한 이미지인지 검사")
    public void validateFile(){
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        //when && then
        assertThatThrownBy(() -> s3Util.validateFile(image)).isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("확장자 검사")
    public void validateExtension(){
        //given
        String image1 = "";
        String image2 = "testImage";
        String image3 = "testImage.gif";
        //when && then
        assertThatThrownBy(() -> s3Util.validateExtension(image1))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> s3Util.validateExtension(image2))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> s3Util.validateExtension(image3))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("서버 저장 이미지 이름 만들기")
    public void generateServerFileName(){
        //given
        String image = "testImage.jpg";

        //when
        String s3FileName = s3Util.generateServerFileName(image);

        //then
        assertThat(image).isNotEqualTo(s3FileName);
    }

    @Test
    @DisplayName("확장자 추출")
    public void getExtension(){
        //given
        String imageUrl = "https://reviewservice.s3.ap-northeast-2.amazonaws.com/testImage.jpg";

        //when
        String extension = s3Util.getExtension(imageUrl);

        //then
        assertThat(extension).isEqualTo("jpg");
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    public void uploadImage_Success(){
        //given
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "testImage.jpg",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        //when
        String imageUrl = s3Util.uploadImage(image);

        //then
        assertThat(imageUrl).startsWith("https://reviewservice.s3.ap-northeast-2.amazonaws.com/");
    }

    @Test
    @DisplayName("이미지 업로드 실패")
    public void uploadImage_Fail(){
        //given
        MockMultipartFile image1 = new MockMultipartFile(
                "image",
                "testImage",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        MockMultipartFile image2 = new MockMultipartFile(
                "image",
                "",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        MockMultipartFile image3 = new MockMultipartFile(
                "image",
                "testImage.gif",
                "image/jpg",
                new byte[]{0, 0, 0, 0});

        //when && then
        assertThatThrownBy(() -> s3Util.uploadImage(image1))
                .isInstanceOf(S3Exception.class);

        assertThatThrownBy(() -> s3Util.uploadImage(image2))
                .isInstanceOf(S3Exception.class);

        assertThatThrownBy(() -> s3Util.uploadImage(image3))
                .isInstanceOf(S3Exception.class);
    }


}