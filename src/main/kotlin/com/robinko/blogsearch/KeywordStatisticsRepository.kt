package com.robinko.blogsearch

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface KeywordStatisticsRepository: JpaRepository<KeywordStatistics, String> {

    /**
     * FYI, 트랜잭션 경합에 따른 통계데이터 갱신의 회귀를 피하기 위해 네이티브 쿼리를 사용하여 직전 데이터의 +1로 갱신.
     */
    @Modifying
    @Query(value = "UPDATE KeywordStatistics SET searchCount = searchCount + 1 WHERE keyword = :keyword")
    fun updateSearchCount(@Param("keyword") keyword: String): Int

    fun findTop10ByOrderBySearchCountDescUpdatedTimeDesc(): List<KeywordStatistics>
}
