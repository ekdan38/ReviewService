# 리뷰 서비스 
### 개발 환경 
SpringBoot 3.4.0 <br> 
Java 17 <br>
Mysql <br>
H2

### docker-compose 기반 인프라 설정
SpringBoot prot : 8080 <Br>
Mysql : 3306 

## 요구 사항 처리
### 1. 리뷰는 존재하는 상품에만 작성 가능
<p> 1. 리뷰 작성을 원하는 상품 조회 이후 로직 처리 </p>
<p> 2. 리뷰 작성을 원하는 상품이 존재하지 않으면 404 응답처리 </p>
<br>

### 2. 유저는 하나의 상품에 대해 하나의 리뷰 작성 가능
<p> 1. 해당 상품에 리뷰를 작성했었는지 확인 </p>
<p> 2. 이미 리뷰를 작성했었다면 400 응답처리 </p>
<br>

### 3. 유저는 1 ~ 5점 사이의 점수와 리뷰 작성 가능
<p> 1. BeanValidation을 통해 점수가 1 ~ 5점 사이인지 확인 </p>
<p> 2. 1 ~ 5점 사이 범위가 아니라면 400 응답처리 </p>
<br>

### 4. 사진은 선택적으로 업로드 가능, S3에 저장한다고 가정하고 dummy구현체 생성
<p> 1.1 사진은 필수값이 아니도록 설정 </p>
<p> 1.2 사진이 지원하지 않는 확장자거나 잘못된 이미지 파일이라면 400 응답처리 </p>
<p> 2.1 사진 검사하는 로직을 포함한 추상 클래스 생성하여 dummy구현체 구현 </p>
<p> 2.2 S3 이미지 업로드시 return 값과 동일하게 return 값 작성 </p>
<p> ex) "https://reviewservice.s3.ap-northeast-2.amazonaws.com/" + S3FileName; </p>
<br>

### 5. 상품 테이블에 reviewCount, score 반영
<p> 1. 리뷰 생성시 해당하는 상품의 reviewCount, score 증가 </p>
<p> 2. 테스트 코드를 통해 100개의 스레드가 같은 상품에 동시에 리뷰 작성시 동시성 문제로 리뷰가 정상적으로 생성되지 않음 확인 </p>
<br> 
<p> => 리뷰 생성은 정상처리 되지만 상품의 reviewCount, score 반영이 제대로 이루어지지 않음 </p>
<p> => 상품에 대해 조회 시점부터 락을 걸어 읽기, 쓰기 작업에 락이 필요하다고 판단 </p>
<br>

<p>  <비관적 락> </p>
    데이터에 잠금을 걸어 다른 트랜잭션의 접근 차단. <br>
    충돌 가능성이 높을때 적합, 데이터 정합성 보장, 락으로 인해 성능 저하 가능성
    <br> <br>


<p>  <낙관적 락> </p>
    데이터의 버전을 확인하여 충돌 방지 <br>
충돌이 드물거나 거의 없는 경우 성능이 좋음, 충돌 발생시 재시도, 높은 충돌에서는 크게 성능 저하    <br>
    <br>


<p>  <비관적 락 선택>  </p>
   동일한 상품에 대해 리뷰를 작성하는 경우가 많이 없을거라고 판단, 다만 충돌 횟수가 많아지면 낙관적 락은 크게 성능 저하가 있다. <br>
    reviewCount와 score를 업데이트 하는 작업은 중요한 데이터 변경 작업이라 생각하여 데이터 정합성을 보장해주는 비관적 락을 선택하였다.
    <br>

### 6. 리뷰는 가장 최근에 작성된 리뷰 순서대로 조회, cursor기반 페이징
<p> 1. JpaAudting으로 생성 날짜 생성 </p>
<p> 2. 생성 날짜는 updateable = false 처리 => Pk Id 값 순서가 생성날짜 순서 (PK는 자동 인덱스 생성) </p>
<p> 3. cursor값 기반으로 가장 최근에 작성된 리뷰의 Id 순으로(생성 날짜) 페이징 </p>
<p> 4. ResponseDto를 통해 예시와 동일하게 출력 (reviewCount => totalCount로 출력) </p>
<br>

### 7. 커스텀 예외
<p> 1. 커스텀 예외를 통해 예외시 응답 처리 </p>

### 에러 코드
| **HTTP 상태 코드** | **에러 코드**    | **메시지**                          |
|--------------------|------------------|-----------------------------------|
| `404 NOT_FOUND`    | `PRODUCT_001`   | 존재하지 않는 상품입니다.         |
| `400 BAD_REQUEST`  | `REVIEW_001`    | 이미 해당 상품에 리뷰를 작성했습니다. |
| `400 BAD_REQUEST`  | `S3_001`        | 이미지 파일이 비어 있습니다.       |
| `400 BAD_REQUEST`  | `S3_002`        | 확장자가 없습니다.                |
| `400 BAD_REQUEST`  | `S3_003`        | 지원하지 않는 확장자입니다.        |

<br>
    
### 8. 테스트 코드 작성
<p> 1. controller, service, repository, s3에 대한 단위 테스트 작성 </p>
<p> 2. 통합 테스트 작정 </p>
<p> 3. 테스트시 H2 사용</p>