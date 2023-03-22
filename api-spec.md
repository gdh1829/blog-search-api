# API 명세서
- 스웨거 API 명세를 확인하기 위해서는 애플리케이션 구동 필수!
- swagger: http://localhost:8080/swagger-ui/index.html

## GET /blogs?query={}&page={}&size={}&sort={}
- 카카오/네이버 등의 블로그 소스로부터 query를 질의한 결과를 반환하며, BlogSearchEvent를 발생시키는 API.
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
- 상세 명세 Swagger 참조
  - http://localhost:8080/swagger-ui/index.html#/keyword-statistics-controller/getTop10KeywordsUsingGET

## DELETE /admin/keywordStatistics/{keyword} 
- 블로그 검색에 따른 키워드 통계 데이터중 특정 키워드 통계 데이터 소프트 삭제 API.
- path variable
  - keyword - required: 삭제 대상 키워드.
- 상세 명세 Swagger 참조
  - http://localhost:8080/swagger-ui/index.html#/keyword-statistics-controller/deleteKeywordStatisticsUsingDELETE
