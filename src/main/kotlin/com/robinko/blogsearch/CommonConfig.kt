package com.robinko.blogsearch

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.task.TaskSchedulerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.retry.annotation.EnableRetry
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@EnableCaching
@EnableTransactionManagement
@EnableRetry
@EnableAsync
// @EnableJpaRepositories(basePackages = ["com.robinko.blogsearch"])
@EnableScheduling
@EnableEncryptableProperties
@ComponentScan(basePackageClasses = [CommonConfig::class])
@Import(
    PropertyEncryptConfig::class
)
class CommonConfig : AsyncConfigurer, TaskSchedulerCustomizer {

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

    override fun customize(taskScheduler: ThreadPoolTaskScheduler?) {
        taskScheduler?.setErrorHandler { t ->
            log.error("@Scheduled error", t)
        }
    }
}
