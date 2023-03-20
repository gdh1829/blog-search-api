package com.robinko.blogsearch

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

/**
 * kotlin nullsafe type support wrapper compatible with java's one
 */
fun <T> nonNullAny(type: Class<T>): T = Mockito.any<T>(type)

/**
 * kotlin nullsafe type support wrapper compatible with java's one
 */
fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
