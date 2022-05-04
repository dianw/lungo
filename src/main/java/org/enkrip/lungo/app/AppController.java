package org.enkrip.lungo.app;

import org.enkrip.lungo.core.web.AuthorizedRestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@AuthorizedRestController
@RequestMapping("/api/apps")
@ApiResponse(
    responseCode = "200",
    description = "Success",
    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
)
public class AppController {
    private static final String DOCS_TAG = "Apps";

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @Operation(tags = DOCS_TAG, summary = "Get list of applications")
    @GetMapping
    public Page<AppData> findAppByCurrentUser(Authentication auth, Pageable pageable) {
        return appService.findAppByCurrentUser(auth, pageable);
    }

    @Operation(tags = DOCS_TAG, summary = "Get application by ID")
    @GetMapping("/{id}")
    public ResponseEntity<AppData> findAppById(@PathVariable("id") Long id) {
        return appService.findAppById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(tags = DOCS_TAG, summary = "Create new application")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppData saveApp(@RequestBody AppData appData) {
        return appService.saveApp(appData);
    }

    @Operation(tags = DOCS_TAG, summary = "Update existing application")
    @PutMapping("/{id}")
    public ResponseEntity<AppData> updateApp(@PathVariable("id") Long id, Authentication auth, @RequestBody AppData appData) {
        return appService.updateApp(id, auth, appData)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
