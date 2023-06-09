# search-blog-api
- 외부 블로그 검색 연동 애플리케이션입니다.

## Jar 파일 다운로드 링크
- https://drive.google.com/file/d/1HtZ7DvIrDZfSjvc13IsUq2K0vpqSD802/view?usp=sharing

## API 명세
- [api-spec.md](./api-spec.md) 파일 참조.
- 디폴트 데이터가 초기 기동과 함께 셋팅되기 때문에 로컬에서 기동 후 swagger을 통해 명세 확인 및 실행해볼 수 있다.

## 애플리케이션 기동 전 사전 설치 사항: Mac 기준
- awscli
```shell
brew install awscli

# 애플리케이션을 기동하는 환경에 awscli를 처음사용하는 환경이라면 아래의 커맨드를 통해 awscli profile 생성 필요.
# !!반드시 region은 ap-northeast-2로 설정!!
aws configure
```
- localstack의 sns, sqs 서비스를 사용하므로 기동전 다음의 인프라 셋팅이 필요.
```shell
# 프로젝트 root 위치에서 다음의 스크립트를 실행하여 로컬스택 기동.
docker-compose up -d
# sqs 큐 생성
aws --endpoint-url http://localhost:4566 sqs create-queue --queue-name 'blog-search-api'
# sns 토픽 생성
aws --endpoint-url http://localhost:4566 sns create-topic --name KeywordStatistics
# sns 토픽 구독
aws --endpoint-url http://localhost:4566 sns subscribe \
--topic-arn arn:aws:sns:ap-northeast-2:000000000000:KeywordStatistics \
--protocol sqs \
--attributes '{"RawMessageDelivery":"true"}' \
--notification-endpoint 'arn:aws:sqs:ap-northeast-2:000000000000:blog-search-api'
```
- localstack 인프라 서비스 점검.
```shell
# sns topic 여부 확인
aws --endpoint-url http://localhost:4566 sns list-topics
# sqs queue 여부 확인
aws --endpoint-url http://localhost:4566 sqs get-queue-attributes --queue-url 'http://localhost:4566/000000000000/blog-search-api' --attribute-names All
# sns topic 구독 현황 확인
aws --endpoint-url http://localhost:4566 sns list-subscriptions-by-topic --topic-arn arn:aws:sns:ap-northeast-2:000000000000:KeywordStatistics
```

## 외부 라이브러리 디펜던시
- localstack docker: sns,sqs
  - 사용목적
    - aws의 sns, sqs 메시징 서비스를 대신하기 위하여 사용합니다.
    - 확장성 있는 다중 애플리케이션의 DB 인기 검색어 통계 갱신 경쟁을 낮추고, 안정적이고 서버 다운이나 예외 발생시 갱신 이벤트 소실 없는 통계 처리를 위하여 사용합니다.  
- aws-java-sdk-sns/sqs
  - localstack의 sns, sqs 서비스를 이용하기 위한 sdk
- spring-cloud-netflix-hystrix
  - 외부 연동 블로그 서비스인 카카오/네이버에서 장애가 발생시 내부로 전파 차단하고 빠르게 다음 우선순위 검색 소스로 넘어가기 위해 사용합니다. 
- shedlock
  - 현재 서버 인메모리 캐시를 사용하고 있지만 다중 애플리케이션 환경에서 외부 공용 캐시 서버가 따로 있다는 전제하에 다중 애플리케이션이 동시간 스케줄에 모두 함께 동작하는 것을 막고,
  - 하나의 서버에서만 스케줄 동작을 진행할 수 있도록 합니다.
  - Top10Keywords 데이터를 뽑아내어 캐싱하는 작업에 스케줄러락을 잡아주기 위한 목적입니다.
- KOMORAN
  - 한국어 형태소 분석기로서, 유저의 블로그 검색 쿼리에 대하여 형태소 분석을 하여 명사형 어휘를 뽑아 키워드를 만들고, 이를 키워드 통계에 반영하기 위해 사용합니다.
- jasypt-spring-boot-starter
  - application.yml 파일에 기재되는 비밀번호/토큰과 같은 민감정보를 암호화합니다.
- swagger
  - API 명세 문서 자동화 목적.

## 필수 요구사항 외 추가 구현 사항

### 블로그 검색 소스(카카오/네이버/etc) 관련
#### DB 데이터 조회 기능.
- BlogSearchPriority Entity
  - 블로그 검색 소스 우선 순위가 데이터로 관리된다.
  - on/off 또는 priority 속성을 갖는다.
#### 우선순위 스위칭 기능.
- 블로그 검색 소스 스위칭
  - 서킷브레이커를 통해 우선 검색 소스의 장애 발생시 차단을 통해 다음 차순위의 검색 소스가 사용되지만,
  - 외부 연동 서비스의 전면적인 장기 장애로 확정시, 서킷브레이커에 불필요한 의존보다는 
  - 검색 소스 우선순위 데이터를 통해 우선순위 변경을 통해 검색 소스를 원천적으로 스위칭 가능.
- 방식
  - 초기화
    - ApplicationReadyEvent를 통해 애플리케이션은 최초 기동시 검색소스전략을 DB로부터 데이터르 가져와 초기화.
  - 스위칭
    - 컨테이너화된 서비스라면 Blue/Green Deloyment를 통해 다중 서비스들이 새로 올라오며 BlogSearchPriority DB 조회를 통해 새로운 검색 소스로 스위칭.
    - 또는 `GET /blogSearchPriority?refresh=true API`를 통하여 직접 해당 컨테이너에 요청을 넣는 방식으로 런타임으로 검색소스우선순위를 바로 적용 또한 가능.

### 검색 키워드 집계 관련
#### 유저 질의어에 대한 키워드 정제
- KOMORAN 한국어 형태소 분석기를 적용하여 유저의 질의 그 자체로 집계하는 것이 아닌 형태소를 분석하여 의미있는 키워드로 정제하여 추출 후 키워드 검색 횟수 통계 집계
#### 메시징 pub/sub을 사용한 검색 횟수 통계 데이터 안정적 분산 갱신
- 블로그 검색시 응답과 별개로 블로그 검색 이벤트를 SNS를 통하여 메시지 발행하고, 
- SQS로 전달된 SNS 메시지는 큐에 저장되어 큐서비스를 통하여 각 서버들이 10개씩 batch 분산 소비를 실시하는 패턴
- 이를 통해 데이터 갱신 경쟁을 낮추고, 데이터 처리 과정에서 예외가 발생하더라도 큐 메시지는 다시 큐로 반납되어 재처리를 안정적으로 소화 가능.
- 서비스가 긴급하게 다운되더라도 외부 큐에 안전하게 보관된 데이터의 장점.
#### 키워드 삭제 기능
- 유저의 질의에 따라 키워드로 분석되어 키워드 집계가 이루어지는데, 실운영 상황이라면 선전성/광고성 등의 키워드가 조작될 수도 있을 가능성이 있다고 본다.
- 이에 따라 필요시 해당 키워드가 노출되지 않도록 삭제 기능을 지원.
- 삭제 기능은 DB에서 해당 키워드 통계 데이터를 완전히 삭제하지 않고 soft delete 처리.
