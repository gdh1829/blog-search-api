package com.robinko.blogsearch

import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.time.ZoneOffset

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    private val zoneOffset = ZoneOffset.UTC

    @ExceptionHandler(Exception::class)
    fun handleExceptions(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<Any> {
        val status = when(ex) {
            is ResponseStatusException -> ex.status
            is ConstraintViolationException -> HttpStatus.BAD_REQUEST
            is AccessDeniedException -> HttpStatus.FORBIDDEN
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return handleExceptionInternal(ex, null, HttpHeaders(), status, request)
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val now = LocalDateTime.now(zoneOffset)
        val message = when {
            ex is ResponseStatusException -> ex.reason ?: ex.localizedMessage
            ex is MethodArgumentNotValidException ->
                ex.allErrors.map { "${it.defaultMessage}" }.joinToString("\n")
            status.is5xxServerError -> "내부 서버 오류. 관리자에게 문의하세요."
            status.is4xxClientError -> "잘못된 요청입니다."
            else -> "알 수 없는 오류. 관리자에게 문의하세요."
        }

        val apiError = ApiError(status.value(), status.reasonPhrase, now, message)

        return super.handleExceptionInternal(ex, body ?: apiError, headers, status, request)
    }
}
