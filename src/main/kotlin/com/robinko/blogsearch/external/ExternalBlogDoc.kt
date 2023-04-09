package com.robinko.blogsearch.external

import com.robinko.blogsearch.BlogDoc

/**
 * 외부 연동 데이터 인터페이스.
 */
interface ExternalBlogDoc {

    /**
     * 외부 연동 데이터를 애플리케이션 공통의 BlogDoc으로 변환.
     */
    fun toBlogDoc(): BlogDoc
}

