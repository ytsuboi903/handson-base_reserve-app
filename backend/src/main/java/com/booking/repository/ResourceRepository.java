package com.booking.repository;

import com.booking.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Resource entity
 */
@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    /**
     * Find all available resources
     * 
     * @param available availability status
     * @return list of resources with specified availability
     */
    List<Resource> findByAvailable(Boolean available);
    
    /**
     * Find resources by name containing the search term (case-insensitive)
     * 
     * @param name search term for resource name
     * @return list of matching resources
     */
    List<Resource> findByNameContainingIgnoreCase(String name);
}

