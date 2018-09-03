package pw.io.booker.controller;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.exception.BookerServiceException;
import pw.io.booker.model.Service;
import pw.io.booker.model.TravelPackage;
import pw.io.booker.repo.ServiceRepository;
import pw.io.booker.repo.TravelPackageRepository;

@RestController
@Transactional
@RequestMapping("/travel-packages/{travelPackageId}/services")
public class TravelPackageServiceController {

  private TravelPackageRepository travelPackageRepository;
  private ServiceRepository serviceRepository;

  public TravelPackageServiceController(TravelPackageRepository travelPackageRepository,
      ServiceRepository serviceRepository) {
    super();
    this.travelPackageRepository = travelPackageRepository;
    this.serviceRepository = serviceRepository;
  }

  @GetMapping
  public List<Service> getAll(@PathVariable("travelPackageId") int travelPackageId,
		  @RequestHeader("Authentication-Token") String token) {
    return travelPackageRepository.findById(travelPackageId).get().getAvailableServiceList();
  }

  @PostMapping
  public List<Service> saveAll(@PathVariable("travelPackageId") int travelPackageId,
      @RequestBody List<Service> services, @RequestHeader("Authentication-Token") String token) {
    for(Service service : services) {
      if(serviceRepository.findById(service.getServiceId()).isPresent()) {
        throw new BookerServiceException("Services already exist");
      }
    }
    TravelPackage travelPackage = travelPackageRepository.findById(travelPackageId).get();
    travelPackage.getAvailableServiceList().addAll(services);
    return travelPackageRepository.save(travelPackage).getAvailableServiceList();
  }

  @PutMapping
  public List<Service> updateAll(@PathVariable("travelPackageId") int travelPackageId,
      @RequestBody List<Service> services, @RequestHeader("Authentication-Token") String token) {
    for (Service service: services) {
      if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
        throw new BookerServiceException("Service should exist first");
      }
    }
    return (List<Service>) serviceRepository.saveAll(services);
  }

  @DeleteMapping
  public List<Service> deleteAll(@PathVariable("travelPackageId") int travelPackageId,
		  @RequestHeader("Authentication-Token") String token) {
    List<Service> availableServiceList =
    travelPackageRepository.findById(travelPackageId).get().getAvailableServiceList();
    serviceRepository.deleteAll(availableServiceList);
    return availableServiceList;
  }

  @GetMapping("/{serviceId}")
  public Service getService(@PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId, @RequestHeader("Authentication-Token") String token) {
    return serviceRepository.findById(serviceId).get();
  }

  @PutMapping("/{serviceId}")
  public Service updateService(@PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId, @RequestBody Service service,
      @RequestHeader("Authentication-Token") String token) {
    if(serviceId != service.getServiceId()) {
      throw new BookerServiceException("Id is not the same with the object id");
    }
    if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
      throw new BookerServiceException("Service should exist first");
    }
    service.setServiceId(serviceId);
    return serviceRepository.save(service);
  }

  @DeleteMapping("/{serviceId}")
  public Service deleteService(@PathVariable("travelPackageId") int travelPackageId,
      @PathVariable("serviceId") int serviceId, @RequestHeader("Authentication-Token") String token) {
    Service service = serviceRepository.findById(serviceId).get();
    serviceRepository.deleteById(serviceId);
    return service;
  }
}
