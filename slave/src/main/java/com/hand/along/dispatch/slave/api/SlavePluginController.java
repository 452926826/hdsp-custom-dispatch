package com.hand.along.dispatch.slave.api;

import com.hand.along.dispatch.slave.app.service.SlavePluginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{organizationId}/plugin")
public class SlavePluginController {
    private final SlavePluginService salvePluginService;

    public SlavePluginController(SlavePluginService salvePluginService) {
        this.salvePluginService = salvePluginService;
    }

    @GetMapping("/reload")
    public ResponseEntity<?> reload(@PathVariable(name = "organizationId") Long tenantId) {
        salvePluginService.reload();
        return ResponseEntity.ok("success");
    }
}
