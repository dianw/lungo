package xyz.mainapi.dashboard.core.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@ApiResponses({
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
@SecuritySchemes({
    @SecurityScheme(
        name = "oidc",
        type = SecuritySchemeType.OPENIDCONNECT,
        openIdConnectUrl = "https://${auth0.domain}/.well-known/openid-configuration"
    )
})
@SecurityRequirements({
    @SecurityRequirement(name = "oidc")
})
public @interface AuthorizedRestController {
    @AliasFor(annotation = RestController.class)
    String value() default "";
}

