package com.booking.controller;

import com.booking.model.Resource;
import com.booking.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Resource management
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * Get all resources
     * 
     * @return list of all resources
     */
    @GetMapping
    public ResponseEntity<List<Resource>> getAllResources(
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) String search) {
        
        List<Resource> resources;
        
        if (search != null && !search.trim().isEmpty()) {
            resources = resourceService.searchResourcesByName(search);
        } else if (available != null && available) {
            resources = resourceService.getAvailableResources();
        } else {
            resources = resourceService.getAllResources();
        }
        
        return ResponseEntity.ok(resources);
    }

    /**
     * Get resource by ID
     * 
     * @param id resource ID
     * @return resource details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResourceById(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new resource
     * 
     * @param resource resource to create
     * @return created resource
     */
    @PostMapping
    public ResponseEntity<Resource> createResource(@Valid @RequestBody Resource resource) {
        try {
            Resource created = resourceService.createResource(resource);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing resource
     * 
     * @param id resource ID
     * @param resource updated resource details
     * @return updated resource
     */
    @PutMapping("/{id}")
    public ResponseEntity<Resource> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody Resource resource) {
        try {
            Resource updated = resourceService.updateResource(id, resource);
            return ResponseEntity.ok(updated);
        } catch (ResourceService.ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a resource
     * 
     * @param id resource ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        try {
            resourceService.deleteResource(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceService.ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

