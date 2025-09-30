package com.orb.gateway.auth.config;

import com.orb.gateway.auth.common.annotation.ApiErrorCodeExample;
import com.orb.gateway.auth.common.annotation.AuthMemberHeaderInfo;
import com.orb.gateway.auth.common.constraint.ErrorType;
import com.orb.gateway.auth.common.constraint.RequestHeaderType;
import com.orb.gateway.auth.common.model.CommonResponse;
import com.orb.gateway.auth.common.model.ExampleHolder;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;


/**
 * Swagger springdoc-ui 구성 파일
 */
@OpenAPIDefinition(
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(url = "http://localhost:8088", description = "local")
        })
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwt token";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        Info info = new Info()
            .version("v0.0.1")
            .title("Orb API")
            .description("Orb API 명세입니다.");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodeExample apiErrorCodeExample = handlerMethod.getMethodAnnotation(ApiErrorCodeExample.class);

            boolean isDeviceHeader = Arrays.stream(handlerMethod.getMethodParameters()).anyMatch(parameter -> parameter.getParameterAnnotation(AuthMemberHeaderInfo.class) != null);

            if (apiErrorCodeExample != null) {
                generateErrorCodeResponseExample(operation, apiErrorCodeExample.value());
            }

            if (isDeviceHeader) {
                addAuthMemberDeviceInfoHeaders(operation);
            }

            return operation;
        };
    }

    private void generateErrorCodeResponseExample(Operation operation, ErrorType[] errorTypes) {
        ApiResponses responses = operation.getResponses();

        List<ErrorType> errorTypesList = Arrays.asList(errorTypes);

        ErrorType[] filteredErrorTypes = Arrays.stream(ErrorType.values())
                .filter(errorType -> errorTypesList.contains(errorType) || errorType.getStatus().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .sorted(Comparator.comparing(ErrorType::getStatus))  // Sort based on HttpStatus
                .toArray(ErrorType[]::new);

        Map<HttpStatus, List<ExampleHolder>> statusWithExampleHolders =
                Arrays.stream(filteredErrorTypes)
                        .map(errorCode -> ExampleHolder.builder()
                                        .statusCode(errorCode.getStatus())
                                        .holder(getSwaggerExample(errorCode))
                                        .errorCode((errorCode.getCode()))
                                        .errorMessage(errorCode.getMessage())
                                        .build())
                        .collect(groupingBy(ExampleHolder::getStatusCode));

        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private void addExamplesToResponses(ApiResponses responses, Map<HttpStatus, List<ExampleHolder>> statusWithExampleHolders) {
        statusWithExampleHolders.forEach((status, v) -> {
            Content content = new Content();
            MediaType mediaType = new MediaType();

            ApiResponse apiResponse = new ApiResponse();

            v.forEach(exampleHolder -> mediaType.addExamples(String.valueOf(exampleHolder.getErrorCode()), exampleHolder.getHolder()));

            content.addMediaType("application/json", mediaType);
            apiResponse.setContent(content);

            responses.addApiResponse(status.toString(), apiResponse);
        });
    }

    private Example getSwaggerExample(ErrorType errorReason) {
        int code = errorReason.getStatus().value() * 10000 + errorReason.getCode();
        CommonResponse.ResFailPattern errorResponse = CommonResponse.ResFailPattern.builder()
                .title(errorReason.getTitle())
                .code(code)
                .message(errorReason.getMessage())
                .build();
        Example example = new Example();
        example.setValue(errorResponse);
        return example;
    }

    private void addAuthMemberDeviceInfoHeaders(Operation operation) {
        // AuthMemberDeviceInfo의 각 필드를 헤더로 추가
        operation.addParametersItem(createHeaderParameter(RequestHeaderType.OS_TYPE));
        operation.addParametersItem(createHeaderParameter(RequestHeaderType.BUILD_VERSION));
        operation.addParametersItem(createHeaderParameter(RequestHeaderType.APP_UID));
        operation.addParametersItem(createHeaderParameter(RequestHeaderType.OS_VERSION));
        operation.addParametersItem(createHeaderParameter(RequestHeaderType.DEVICE_MODEL));
    }

    private Parameter createHeaderParameter(RequestHeaderType header) {
        return new Parameter()
                .name(header.getHeaderName())
                .in("header")
                .description(header.getHeaderDesc())
                .example(header.getSample())
                .required(true)
                .schema(new StringSchema());
    }
}