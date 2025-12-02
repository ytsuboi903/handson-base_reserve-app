package com.booking.service;

import com.booking.model.Resource;
import com.booking.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ResourceService
 */
@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = createTestResource();
    }

    // ========== Read Operations ==========

    @Test
    void should_returnAllResources_when_getAllResources() {
        // Arrange
        List<Resource> expectedResources = Arrays.asList(testResource, createTestResource(2L, "会議室B"));
        when(resourceRepository.findAll()).thenReturn(expectedResources);

        // Act
        List<Resource> result = resourceService.getAllResources();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedResources);
        verify(resourceRepository).findAll();
    }

    @Test
    void should_returnEmptyList_when_noResourcesExist() {
        // Arrange
        when(resourceRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Resource> result = resourceService.getAllResources();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(resourceRepository).findAll();
    }

    @Test
    void should_returnResource_when_resourceExists() {
        // Arrange
        Long resourceId = 1L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(testResource));

        // Act
        Optional<Resource> result = resourceService.getResourceById(resourceId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testResource);
        verify(resourceRepository).findById(resourceId);
    }

    @Test
    void should_returnEmpty_when_resourceNotFound() {
        // Arrange
        Long resourceId = 999L;
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        // Act
        Optional<Resource> result = resourceService.getResourceById(resourceId);

        // Assert
        assertThat(result).isEmpty();
        verify(resourceRepository).findById(resourceId);
    }

    @Test
    void should_returnAvailableResources_when_availableIsTrue() {
        // Arrange
        List<Resource> expectedResources = Arrays.asList(testResource);
        when(resourceRepository.findByAvailable(true)).thenReturn(expectedResources);

        // Act
        List<Resource> result = resourceService.getAvailableResources();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedResources);
        verify(resourceRepository).findByAvailable(true);
    }

    @Test
    void should_returnEmptyList_when_noAvailableResources() {
        // Arrange
        when(resourceRepository.findByAvailable(true)).thenReturn(Collections.emptyList());

        // Act
        List<Resource> result = resourceService.getAvailableResources();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(resourceRepository).findByAvailable(true);
    }

    @Test
    void should_returnResources_when_nameMatches() {
        // Arrange
        String searchTerm = "会議室";
        List<Resource> expectedResources = Arrays.asList(testResource);
        when(resourceRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(expectedResources);

        // Act
        List<Resource> result = resourceService.searchResourcesByName(searchTerm);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).containsExactlyElementsOf(expectedResources);
        verify(resourceRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    @Test
    void should_returnEmptyList_when_nameDoesNotMatch() {
        // Arrange
        String searchTerm = "存在しないリソース";
        when(resourceRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(Collections.emptyList());

        // Act
        List<Resource> result = resourceService.searchResourcesByName(searchTerm);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(resourceRepository).findByNameContainingIgnoreCase(searchTerm);
    }

    // ========== Create Operation ==========

    @Test
    void should_createResource_when_validDataProvided() {
        // Arrange
        Resource newResource = createTestResource();
        newResource.setId(null); // New resource should not have ID
        when(resourceRepository.save(newResource)).thenReturn(testResource);

        // Act
        Resource result = resourceService.createResource(newResource);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        verify(resourceRepository).save(newResource);
    }

    @Test
    void should_throwException_when_resourceHasId() {
        // Arrange
        Resource resourceWithId = createTestResource();
        resourceWithId.setId(1L);

        // Act & Assert
        assertThatThrownBy(() -> resourceService.createResource(resourceWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("New resource should not have an ID");
        
        verify(resourceRepository, never()).save(any());
    }

    // ========== Update Operation ==========

    @Test
    void should_updateResource_when_validDataProvided() {
        // Arrange
        Long resourceId = 1L;
        Resource existingResource = createTestResource();
        Resource updatedData = createTestResource();
        updatedData.setName("更新された会議室");
        updatedData.setDescription("更新された説明");
        updatedData.setCapacity(20);
        updatedData.setAvailable(false);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(existingResource));
        when(resourceRepository.save(any(Resource.class))).thenReturn(updatedData);

        // Act
        Resource result = resourceService.updateResource(resourceId, updatedData);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("更新された会議室");
        assertThat(result.getDescription()).isEqualTo("更新された説明");
        assertThat(result.getCapacity()).isEqualTo(20);
        assertThat(result.getAvailable()).isFalse();
        verify(resourceRepository).findById(resourceId);
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void should_throwException_when_resourceNotFoundForUpdate() {
        // Arrange
        Long resourceId = 999L;
        Resource updatedData = createTestResource();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> resourceService.updateResource(resourceId, updatedData))
                .isInstanceOf(ResourceService.ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found with id: " + resourceId);
        
        verify(resourceRepository, never()).save(any());
    }

    // ========== Delete Operation ==========

    @Test
    void should_deleteResource_when_resourceExists() {
        // Arrange
        Long resourceId = 1L;
        when(resourceRepository.existsById(resourceId)).thenReturn(true);
        doNothing().when(resourceRepository).deleteById(resourceId);

        // Act
        resourceService.deleteResource(resourceId);

        // Assert
        verify(resourceRepository).existsById(resourceId);
        verify(resourceRepository).deleteById(resourceId);
    }

    @Test
    void should_throwException_when_resourceNotFoundForDelete() {
        // Arrange
        Long resourceId = 999L;
        when(resourceRepository.existsById(resourceId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> resourceService.deleteResource(resourceId))
                .isInstanceOf(ResourceService.ResourceNotFoundException.class)
                .hasMessageContaining("Resource not found with id: " + resourceId);
        
        verify(resourceRepository, never()).deleteById(any());
    }

    // ========== Helper Methods ==========

    private Resource createTestResource() {
        return createTestResource(1L, "会議室A");
    }

    private Resource createTestResource(Long id, String name) {
        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(name);
        resource.setDescription("テスト用の会議室");
        resource.setCapacity(10);
        resource.setAvailable(true);
        return resource;
    }
}

