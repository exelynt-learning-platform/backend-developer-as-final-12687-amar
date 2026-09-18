package com.amar.resource_booking.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.amar.resource_booking.dto.ResourceRequest;
import com.amar.resource_booking.dto.ResourceResponse;
import com.amar.resource_booking.service.ResourceService;

class ResourceControllerTest {

    private ResourceService resourceService;
    private ResourceController resourceController;

    @BeforeEach
    void setUp() {
        resourceService = mock(ResourceService.class);
        resourceController = new ResourceController(resourceService);
    }

    @Test
    void createResourceShouldReturnCreated() {

        ResourceRequest request = mock(ResourceRequest.class);
        ResourceResponse expectedResponse =
                mock(ResourceResponse.class);

        when(resourceService.createResource(request))
                .thenReturn(expectedResponse);

        ResponseEntity<ResourceResponse> response =
                resourceController.createResource(request);

        assertEquals(
                HttpStatus.CREATED,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void getAllResourcesShouldReturnOk() {

        List<ResourceResponse> resources =
                List.of(mock(ResourceResponse.class));

        when(resourceService.getAllResources())
                .thenReturn(resources);

        ResponseEntity<List<ResourceResponse>> response =
                resourceController.getAllResources();

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                resources,
                response.getBody()
        );
    }

    @Test
    void getResourceByIdShouldReturnOk() {

        ResourceResponse expectedResponse =
                mock(ResourceResponse.class);

        when(resourceService.getResourceById(1L))
                .thenReturn(expectedResponse);

        ResponseEntity<ResourceResponse> response =
                resourceController.getResourceById(1L);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void updateResourceShouldReturnOk() {

        ResourceRequest request = mock(ResourceRequest.class);
        ResourceResponse expectedResponse =
                mock(ResourceResponse.class);

        when(resourceService.updateResource(1L, request))
                .thenReturn(expectedResponse);

        ResponseEntity<ResourceResponse> response =
                resourceController.updateResource(1L, request);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertSame(
                expectedResponse,
                response.getBody()
        );
    }

    @Test
    void deleteResourceShouldReturnNoContent() {

        doNothing()
                .when(resourceService)
                .deleteResource(1L);

        ResponseEntity<Void> response =
                resourceController.deleteResource(1L);

        assertEquals(
                HttpStatus.NO_CONTENT,
                response.getStatusCode()
        );

        verify(resourceService)
                .deleteResource(1L);
    }
}