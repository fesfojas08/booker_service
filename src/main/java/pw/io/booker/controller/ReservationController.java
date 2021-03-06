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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.exception.BookerServiceException;
import pw.io.booker.model.Image;
import pw.io.booker.model.Reservation;
import pw.io.booker.model.Service;
import pw.io.booker.repo.CustomerRepository;
import pw.io.booker.repo.ImageRepository;
import pw.io.booker.repo.ReservationRepository;
import pw.io.booker.repo.ServiceRepository;

@RestController
@Transactional
@RequestMapping("/reservations")
public class ReservationController {

  private ReservationRepository reservationRepository;
  private ServiceRepository serviceRepository;
  private ImageRepository imageRepository;
  private CustomerRepository customerRepository;
  // private ReservationServiceRepository reservationServiceRepository;


  public ReservationController(ReservationRepository reservationRepository,
      ServiceRepository serviceRepository, ImageRepository imageRepository,
      CustomerRepository customerRepository) {
    // ReservationServiceRepository reservationServiceRepository) {
    super();
    this.reservationRepository = reservationRepository;
    this.serviceRepository = serviceRepository;
    this.imageRepository = imageRepository;
    this.customerRepository = customerRepository;
    // this.reservationServiceRepository = reservationServiceRepository;
  }

  @GetMapping
  public List<Reservation> getAll(@RequestHeader("Authentication-Token") String token) {
    return (List<Reservation>) reservationRepository.findAll();
  }

  @PostMapping
  public List<Reservation> saveAll(@RequestBody List<Reservation> reservations,
		  @RequestHeader("Authentication-Token") String token) {
    for (Reservation reservation : reservations) {
      if (reservationRepository.findById(reservation.getReservationId()).isPresent()) {
        throw new BookerServiceException("Reservations already exist");
      }
      for (Service service : reservation.getAvailedServiceList()) {
        if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
          throw new BookerServiceException("Service doesn't exist");
        }

        for (Image image : service.getImages()) {
          if (!imageRepository.findById(image.getImageId()).isPresent()) {
            throw new BookerServiceException("Image doesn't exist");
          }
        }


        if (!customerRepository.findById(reservation.getCustomer().getCustomerId()).isPresent()) {
          throw new BookerServiceException("Customer doesn't exist");
        }
      }
    }
    return (List<Reservation>) reservationRepository.saveAll(reservations);
  }

  @PutMapping
  public List<Reservation> updateAll(@RequestBody List<Reservation> reservations,
		  @RequestHeader("Authentication-Token") String token) {
    for (Reservation reservation : reservations) {
      if (!reservationRepository.findById(reservation.getReservationId()).isPresent()) {
        throw new BookerServiceException("Reservations should exist first");
      }
      for (Service service : reservation.getAvailedServiceList()) {
        if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
          throw new BookerServiceException("Service doesn't exist");
        }

        for (Image image : service.getImages()) {
          if (!imageRepository.findById(image.getImageId()).isPresent()) {
            throw new BookerServiceException("Image doesn't exist");
          }
        }


        if (!customerRepository.findById(reservation.getCustomer().getCustomerId()).isPresent()) {
          throw new BookerServiceException("Customer doesn't exist");
        }

        reservation.getAvailedServiceList().add(service);
      }
      reservationRepository.save(reservation);
    }
    return reservations;
  }

  @DeleteMapping
  public List<Reservation> deleteAll(
      @RequestParam("reservationIdList") List<Integer> reservationIdList,
      @RequestHeader("Authentication-Token") String token) {
    List<Reservation> reservationList =
        (List<Reservation>) reservationRepository.findAllById(reservationIdList);
    reservationRepository.deleteAll(reservationList);
    return reservationList;
  }

  @GetMapping("/{reservationId}")
  public Reservation getReservation(@PathVariable("reservationId") int reservationId,
		  @RequestHeader("Authentication-Token") String token) {
    return reservationRepository.findById(reservationId).get();
  }

  @PutMapping("/{reservationId}")
  public Reservation updateReservation(@PathVariable("reservationId") int reservationId,
      @RequestBody Reservation reservation, @RequestHeader("Authentication-Token") String token) {
    if (!reservationRepository.findById(reservation.getReservationId()).isPresent()) {
      throw new BookerServiceException("Reservation should exist first");
    }
    for (Service service : reservation.getAvailedServiceList()) {
      if (!serviceRepository.findById(service.getServiceId()).isPresent()) {
        throw new BookerServiceException("Service doesn't exist");
      }

      for (Image image : service.getImages()) {
        if (!imageRepository.findById(image.getImageId()).isPresent()) {
          throw new BookerServiceException("Image doesn't exist");
        }
      }


      if (!customerRepository.findById(reservation.getCustomer().getCustomerId()).isPresent()) {
        throw new BookerServiceException("Customer doesn't exist");
      }

    }
    reservationRepository.save(reservation);
    return reservation;
  }

  @DeleteMapping("/{reservationId}")
  public Reservation deleteReservation(@PathVariable("reservationId") int reservationId,
		  @RequestHeader("Authentication-Token") String token) {
    Reservation reservation = reservationRepository.findById(reservationId).get();
    reservationRepository.delete(reservation);
    return reservation;
  }
}
