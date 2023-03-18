package com.robinko.blogsearch.config

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.AsyncConfigurer

@EnableEncryptableProperties
@ComponentScan(basePackageClasses = [CommonConfig::class])
@Import(PropertyEncryptConfig::class)
class CommonConfig : AsyncConfigurer {

    private val log = LoggerFactory.getLogger(CommonConfig::class.java)

    @Configuration
    @PropertySource("classpath:application.yml", factory = YamlPropertyLoadFactory::class)
    class DefaultConfig

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return AsyncUncaughtExceptionHandler { ex, method, params ->
            log.error("@Async ${method.name}", ex)
            params.forEach {
                log.debug("${ex.localizedMessage} => ${method.name} params: $it")
            }
        }
    }
}
