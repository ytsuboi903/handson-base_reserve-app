package com.booking.service;

import com.booking.model.Resource;
import com.booking.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Resource management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;

    /**
     * Get all resources
     * 
     * @return list of all resources
     */
    @Transactional(readOnly = true)
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    /**
     * Get resource by ID
     * 
     * @param id resource ID
     * @return Optional containing the resource if found
     */
    @Transactional(readOnly = true)
    public Optional<Resource> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    /**
     * Get all available resources
     * 
     * @return list of available resources
     */
    @Transactional(readOnly = true)
    public List<Resource> getAvailableResources() {
        return resourceRepository.findByAvailable(true);
    }

    /**
     * Search resources by name
     * 
     * @param name search term
     * @return list of matching resources
     */
    @Transactional(readOnly = true)
    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Create a new resource
     * 
     * @param resource resource to create
     * @return created resource
     */
    public Resource createResource(Resource resource) {
        if (resource.getId() != null) {
            throw new IllegalArgumentException("New resource should not have an ID");
        }
        return resourceRepository.save(resource);
    }

    /**
     * Update an existing resource
     * 
     * @param id resource ID
     * @param resourceDetails updated resource details
     * @return updated resource
     * @throws ResourceNotFoundException if resource is not found
     */
    public Resource updateResource(Long id, Resource resourceDetails) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));

        resource.setName(resourceDetails.getName());
        resource.setDescription(resourceDetails.getDescription());
        resource.setCapacity(resourceDetails.getCapacity());
        resource.setAvailable(resourceDetails.getAvailable());

        return resourceRepository.save(resource);
    }

    /**
     * Delete a resource
     * 
     * @param id resource ID
     * @throws ResourceNotFoundException if resource is not found
     */
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource not found with id: " + id);
        }
        resourceRepository.deleteById(id);
    }

    /**
     * Custom exception for resource not found
     */
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}

