package xyz.mainapi.dashboard.app;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apps")
public class AppController {
    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @GetMapping
    public Page<AppData> findAppByCurrentUser(Authentication auth, Pageable pageable) {
        return appService.findAppByCurrentUser(auth, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppData> findAppById(@PathVariable("id") Long id) {
        return appService.findAppById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppData saveApp(@RequestBody AppData appData) {
        return appService.saveApp(appData);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppData> updateApp(@PathVariable("id") Long id, Authentication auth, @RequestBody AppData appData) {
        return appService.updateApp(id, auth, appData)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
