package com.hand.along.dispatch.master.api;

import com.hand.along.dispatch.master.app.service.MonitorService;
import com.hand.along.dispatch.master.app.service.WorkflowService;
import com.hand.along.dispatch.master.domain.Workflow;
import com.hand.along.dispatch.common.domain.monitor.MasterMonitorInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{organizationId}/workflow")
public class WorkflowController {
    private final WorkflowService workflowService;
    private final MonitorService monitorService;

    public WorkflowController(WorkflowService workflowService, MonitorService monitorService) {
        this.workflowService = workflowService;
        this.monitorService = monitorService;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> runWorkflow(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody Workflow workflow) {
        workflow.setTenantId(tenantId);
        return ResponseEntity.ok(workflowService.execute(workflow));
    }

    @PostMapping("/cron")
    public ResponseEntity<?> cronWorkflow(@PathVariable(name = "organizationId") Long tenantId,
                                         @RequestBody Workflow workflow) {
        workflow.setTenantId(tenantId);
        workflowService.cron(workflow);
        return ResponseEntity.ok(workflow);
    }

    @GetMapping("/monitor")
    public ResponseEntity<?> monitor(@PathVariable(name = "organizationId") Long tenantId) {
        List<MasterMonitorInfo> masterMonitorInfoList =  monitorService.monitorInfo(tenantId);
        return ResponseEntity.ok(masterMonitorInfoList);
    }

}
