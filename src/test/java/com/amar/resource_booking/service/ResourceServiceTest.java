package com.amar.resource_booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.amar.resource_booking.dto.ResourceRequest;
import com.amar.resource_booking.dto.ResourceResponse;
import com.amar.resource_booking.entity.Resource;
import com.amar.resource_booking.repository.ResourceRepository;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void createResourceShouldSaveResource() {

        ResourceRequest request = new ResourceRequest();
        request.setName("Meeting Room");
        request.setDescription("Test room");
        request.setType("ROOM");
        request.setAvailable(true);
        request.setPrice(new BigDecimal("500.00"));

        when(resourceRepository.save(
                org.mockito.ArgumentMatchers.any(Resource.class)
        )).thenAnswer(invocation -> {

            Resource saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        ResourceResponse response =
                resourceService.createResource(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Meeting Room", response.getName());
        assertEquals("Test room", response.getDescription());
        assertEquals("ROOM", response.getType());
        assertEquals(true, response.isAvailable());
        assertEquals(
                new BigDecimal("500.00"),
                response.getPrice()
        );

        verify(resourceRepository).save(
                org.mockito.ArgumentMatchers.any(Resource.class)
        );
    }
    @Test
    void getResourceByIdShouldReturnResource() {

        Resource resource = new Resource(
                "Conference Room",
                "Large room",
                "ROOM",
                true,
                new BigDecimal("800.00")
        );

        resource.setId(2L);

        when(resourceRepository.findById(2L))
                .thenReturn(Optional.of(resource));

        ResourceResponse response =
                resourceService.getResourceById(2L);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Conference Room", response.getName());
        assertEquals("Large room", response.getDescription());
        assertEquals("ROOM", response.getType());
        assertEquals(true, response.isAvailable());
        assertEquals(
                new BigDecimal("800.00"),
                response.getPrice()
        );

        verify(resourceRepository).findById(2L);
    }
    
    @Test
    void getAllResourcesShouldReturnResources() {

        Resource resource1 = new Resource(
                "Meeting Room",
                "Small meeting room",
                "ROOM",
                true,
                new BigDecimal("500.00")
        );
        resource1.setId(1L);

        Resource resource2 = new Resource(
                "Projector",
                "HD Projector",
                "EQUIPMENT",
                true,
                new BigDecimal("300.00")
        );
        resource2.setId(2L);

        when(resourceRepository.findAll())
                .thenReturn(java.util.List.of(resource1, resource2));

        var responses = resourceService.getAllResources();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Meeting Room", responses.get(0).getName());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Projector", responses.get(1).getName());

        verify(resourceRepository).findAll();
    }
    
    @Test
    void getResourceByIdShouldThrowExceptionWhenNotFound() {

        when(resourceRepository.findById(99L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.amar.resource_booking.exception.ResourceNotFoundException.class,
                () -> resourceService.getResourceById(99L)
        );

        verify(resourceRepository).findById(99L);
    }
    
    @Test
    void updateResourceShouldUpdateResource() {

        Resource existingResource = new Resource(
                "Old Room",
                "Old description",
                "ROOM",
                true,
                new BigDecimal("400.00")
        );

        existingResource.setId(1L);

        ResourceRequest request = new ResourceRequest();
        request.setName("Updated Room");
        request.setDescription("Updated description");
        request.setType("CONFERENCE_ROOM");
        request.setAvailable(false);
        request.setPrice(new BigDecimal("700.00"));

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(existingResource));

        when(resourceRepository.save(existingResource))
                .thenReturn(existingResource);

        ResourceResponse response =
                resourceService.updateResource(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Updated Room", response.getName());
        assertEquals("Updated description", response.getDescription());
        assertEquals("CONFERENCE_ROOM", response.getType());
        assertEquals(false, response.isAvailable());
        assertEquals(
                new BigDecimal("700.00"),
                response.getPrice()
        );

        verify(resourceRepository).findById(1L);
        verify(resourceRepository).save(existingResource);
    }
    
    @Test
    void updateResourceShouldThrowExceptionWhenNotFound() {

        ResourceRequest request = new ResourceRequest();
        request.setName("Updated Room");
        request.setDescription("Updated description");
        request.setType("ROOM");
        request.setAvailable(true);
        request.setPrice(new BigDecimal("500.00"));

        when(resourceRepository.findById(99L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.amar.resource_booking.exception.ResourceNotFoundException.class,
                () -> resourceService.updateResource(99L, request)
        );

        verify(resourceRepository).findById(99L);
    }
    
    @Test
    void deleteResourceShouldDeleteResource() {

        Resource resource = new Resource(
                "Meeting Room",
                "Test room",
                "ROOM",
                true,
                new BigDecimal("500.00")
        );

        resource.setId(1L);

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(resource));

        resourceService.deleteResource(1L);

        verify(resourceRepository).findById(1L);
        verify(resourceRepository).delete(resource);
    }
    
    @Test
    void deleteResourceShouldThrowExceptionWhenNotFound() {

        when(resourceRepository.findById(99L))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                com.amar.resource_booking.exception.ResourceNotFoundException.class,
                () -> resourceService.deleteResource(99L)
        );

        verify(resourceRepository).findById(99L);
    }
}