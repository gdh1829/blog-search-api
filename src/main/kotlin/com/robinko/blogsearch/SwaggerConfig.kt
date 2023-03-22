package com.robinko.blogsearch

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Pageable
import org.springframework.web.servlet.view.InternalResourceViewResolver
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.schema.AlternateTypeRules
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Profile("swagger")
@Configuration
class SwaggerConfig {

    @Bean
    fun swaggerApi(): Docket {
        val typeResolver = TypeResolver()

        return Docket(DocumentationType.SWAGGER_2)
            .consumes(this.getConsumeContentTypes())
            .produces(this.getProduceContentTypes())
            .alternateTypeRules(
                AlternateTypeRules.newRule(
                    typeResolver.resolve(Pageable::class.java),
                    typeResolver.resolve(PageableSwagger::class.java)
                )
            )
            .apiInfo(swaggerInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.robinko.blogsearch"))
            .paths(PathSelectors.any())
            .build()
            .useDefaultResponseMessages(false)
    }

    private fun swaggerInfo(): ApiInfo {
        return ApiInfoBuilder().title("search-blog-api")
            .description("search blog api doc").build()
    }

    private fun getConsumeContentTypes(): Set<String> {
        val consumes: MutableSet<String> = HashSet()
        consumes.add("application/json;charset=UTF-8")
        consumes.add("application/x-www-form-urlencoded")
        return consumes
    }

    private fun getProduceContentTypes(): Set<String> {
        val produces: MutableSet<String> = HashSet()
        produces.add("application/json;charset=UTF-8")
        return produces
    }

    @Bean
    fun defaultViewResolver(): InternalResourceViewResolver {
        return InternalResourceViewResolver()
    }

    @ApiModel
    data class PageableSwagger(
        @ApiModelProperty(value = "페이지 번호")
        val page: Int,
        @ApiModelProperty(value = "페이지 사이즈")
        val size: Int,
        @ApiModelProperty(value = "only allowed:GET/blogSearch->score,latest & GET/blogSearchPriorities->priority,serverId)")
        val sort: String
    )
}
