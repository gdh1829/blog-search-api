package com.robinko.blogsearch

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig
import org.jasypt.iv.RandomIvGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PropertyEncryptConfig {

    @Bean
    fun stringEncryptor(): StringEncryptor {
        val config = EnvironmentStringPBEConfig().apply {
            poolSize = 4
            password = "JJecoqAHKYkcKSvBkzjQDVNfyULTkprH"
            algorithm = "PBEWithHMACSHA512AndAES_128"
            ivGenerator = RandomIvGenerator()
        }

        return PooledPBEStringEncryptor().apply {
            setConfig(config)
        }
    }
}