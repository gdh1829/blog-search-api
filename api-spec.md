# API 명세서
- 스웨거 API 명세를 확인하기 위해서는 애플리케이션 구동 필수!
- swagger: http://localhost:8080/swagger-ui/index.html

## GET /blogs?query={}&page={}&size={}&sort={}
- 카카오/네이버 등의 블로그 소스로부터 query를 질의한 결과를 반환 API.
- 부가적으로 검색 발생시 BlogSearchEvent를 발생시켜 
- 유저의 질의를 형태소 분석하고 주요 키워드를 추출하여 
- aws sns/sqs를 이용한 pub/sub 패턴을 통해 집계가 별도의 쓰레드에서 진행됩니다.
- params
  - query - required: 질의어
  - page - optional: 페이지 번호. default 1
  - size - optional: 페이지 사이즈.  default 20
  - sort - optional: 정렬. score와 latest만 허용.
- 상세 명세 Swagger 참조.
  - http://localhost:8080/swagger-ui/index.html#/blog-search-controller/searchBlogUsingGET

## GET /blogSearchPriorities?page={}&size={}&sort={}
- 외부 연동 블로그 검색 소스 우선 순위 데이터 조회 API. 우선순위 오름차순 정렬.
- params
  - page - optional: 페이지 번호. deafult 0
  - size - optional: 페이지 사이즈. default 10
  - sort - optional: 정렬. serverId와 priority만 허용.
- 상세 명세 Swagger 참조.
  - http://localhost:8080/swagger-ui/index.html#/blog-search-priority-controller/getPagedBlogSearchPrioritiesUsingGET

## PUT /blogSearchPriorities?refresh=true
- 외부 연동 블로그 검색 소스에 대한 DB 데이터 우선순위 변동을 서버에 실시간 적용시키는 API.
- 상세 명세 Swagger 참조
  - http://localhost:8080/swagger-ui/index.html#/blog-search-priority-controller/applyBlogSearchPriorityUsingPUT

## GET /keywordStatistics?top10=true
- 블로그 검색 키워드 조회수 인기 Top10 키워드 통계 조회 API. 
- **트래픽과 데이터 양을 고려하여 실시간 최신 데이터 제공 X** 
  - **캐싱이 적용되어 있으며 스케줄러를 통하여 5분단위로 캐시가 갱신**됨.
  - **키워드 검색 횟수 갱신은 큐 메시징 서비스를 통하여 별도**로 이루어짐.
- 상세 명세 Swagger 참조
  - http://localhost:8080/swagger-ui/index.html#/keyword-statistics-controller/getTop10KeywordsUsingGET

## DELETE /admin/keywordStatistics/{keyword} 
- 블로그 검색에 따른 키워드 통계 데이터중 특정 키워드 통계 데이터 소프트 삭제 API.
- path variable
  - keyword - required: 삭제 대상 키워드.
- 상세 명세 Swagger 참조
  - http://localhost:8080/swagger-ui/index.html#/keyword-statistics-controller/deleteKeywordStatisticsUsingDELETE
