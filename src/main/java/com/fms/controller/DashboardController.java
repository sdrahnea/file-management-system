package com.fms.controller;

import com.fms.model.FileEntity;
import com.fms.model.UsageEvent;
import com.fms.repository.FileRepository;
import com.fms.repository.UsageEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * MVC controller that serves the Thymeleaf-based admin dashboard UI.
 * Accessible at: http://localhost:8081/ui/dashboard
 */
@Controller
@RequestMapping("/ui")
public class DashboardController {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UsageEventRepository usageEventRepository;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false) String tenant,
            Model model) {

        // Fetch files
        List<FileEntity> files = new ArrayList<>();
        fileRepository.findAll().forEach(files::add);

        // Fetch usage events
        List<UsageEvent> events = new ArrayList<>();
        usageEventRepository.findAll().forEach(events::add);

        // Filter by tenant if provided
        if (tenant != null && !tenant.isBlank()) {
            files.removeIf(f -> !tenant.equals(f.getTenant()));
            events.removeIf(e -> !tenant.equals(e.getTenant()));
        }

        // Compute summary stats
        long totalFiles   = files.size();
        long totalUploads = events.stream().filter(e -> e.getEventType() == UsageEvent.EventType.UPLOAD).count();
        long totalDownloads = events.stream().filter(e -> e.getEventType() == UsageEvent.EventType.DOWNLOAD).count();
        long totalStorageBytes = files.stream().mapToLong(f -> f.getFileSizeBytes() != null ? f.getFileSizeBytes() : 0).sum();

        // Distinct tenants for filter dropdown
        List<String> tenants = new ArrayList<>();
        fileRepository.findAll().forEach(f -> {
            if (f.getTenant() != null && !tenants.contains(f.getTenant())) {
                tenants.add(f.getTenant());
            }
        });

        model.addAttribute("files", files);
        model.addAttribute("events", events);
        model.addAttribute("totalFiles", totalFiles);
        model.addAttribute("totalUploads", totalUploads);
        model.addAttribute("totalDownloads", totalDownloads);
        model.addAttribute("totalStorageBytes", totalStorageBytes);
        model.addAttribute("tenants", tenants);
        model.addAttribute("selectedTenant", tenant != null ? tenant : "");

        return "dashboard";
    }
}

