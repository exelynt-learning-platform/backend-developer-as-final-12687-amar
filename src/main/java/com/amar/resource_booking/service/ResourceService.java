package com.amar.resource_booking.service;



import java.util.List;

import org.springframework.stereotype.Service;


import com.amar.resource_booking.dto.ResourceRequest;
import com.amar.resource_booking.dto.ResourceResponse;
import com.amar.resource_booking.entity.Resource;
import com.amar.resource_booking.repository.ResourceRepository;
import com.amar.resource_booking.exception.ResourceNotFoundException;


@Service
public class ResourceService {
	
	
	 private final ResourceRepository resourceRepository;

	    public ResourceService(ResourceRepository resourceRepository) {
	        this.resourceRepository = resourceRepository;
	    }

	    public ResourceResponse createResource(ResourceRequest request) {

	        Resource resource = new Resource();

	        resource.setName(request.getName());
	        resource.setDescription(request.getDescription());
	        resource.setType(request.getType());
	        resource.setAvailable(request.isAvailable());
	        resource.setPrice(request.getPrice());

	        Resource savedResource = resourceRepository.save(resource);

	        return mapToResponse(savedResource);
	    }

	    public List<ResourceResponse> getAllResources() {

	        return resourceRepository.findAll()
	                .stream()
	                .map(this::mapToResponse)
	                .toList();
	    }

	    public ResourceResponse getResourceById(Long id) {

	        Resource resource = resourceRepository.findById(id)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Resource not found with id: " + id));

	        return mapToResponse(resource);
	    }

	    public ResourceResponse updateResource(Long id, ResourceRequest request) {

	        Resource resource = resourceRepository.findById(id)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Resource not found with id: " + id));

	        resource.setName(request.getName());
	        resource.setDescription(request.getDescription());
	        resource.setType(request.getType());
	        resource.setAvailable(request.isAvailable());
	        resource.setPrice(request.getPrice());

	        Resource updatedResource = resourceRepository.save(resource);

	        return mapToResponse(updatedResource);
	    }

	    public void deleteResource(Long id) {

	        Resource resource = resourceRepository.findById(id)
	                .orElseThrow(() ->
	                        new ResourceNotFoundException("Resource not found with id: " + id));

	        resourceRepository.delete(resource);
	    }

	    private ResourceResponse mapToResponse(Resource resource) {

	        return new ResourceResponse(
	                resource.getId(),
	                resource.getName(),
	                resource.getDescription(),
	                resource.getType(),
	                resource.isAvailable(),
	                resource.getPrice()
	        );
	    }

	
	
}
