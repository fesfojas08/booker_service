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
import pw.io.booker.model.Customer;
import pw.io.booker.repo.CustomerRepository;

@RestController
@Transactional
@RequestMapping("/customers")
public class CustomerController {

  private CustomerRepository customerRepository;

  public CustomerController(CustomerRepository customerRepository) {
    super();
    this.customerRepository = customerRepository;
  }

  @GetMapping
  public List<Customer> getAll(@RequestHeader("Authentication-Token") String token) {
    return (List<Customer>) customerRepository.findAll();
  }

  @PostMapping
  public List<Customer> saveAll(@RequestBody List<Customer> customers) throws BookerServiceException {
    for(Customer customer : customers) {
      if(customerRepository.findById(customer.getCustomerId()).isPresent()) {
        throw new BookerServiceException("Customers already exist");
      }
      if(customerRepository.findByUsername(customer.getUsername()).isPresent()) {
    	throw new BookerServiceException("Username already exists");
      }
    }
    return (List<Customer>) customerRepository.saveAll(customers);
  }

  @PutMapping
  public List<Customer> updateAll(@RequestBody List<Customer> customers, 
		  @RequestHeader("Authentication-Token") String token) throws BookerServiceException {
    for(Customer customer : customers) {
      if(!customerRepository.findById(customer.getCustomerId()).isPresent()) {
        throw new BookerServiceException("Customers should exist first");
      }
    }
    return (List<Customer>) customerRepository.saveAll(customers);
  }

  @DeleteMapping
  public List<Customer> deleteAll(@RequestParam("customerIdList") List<Integer> customerIdList, 
		  @RequestHeader("Authentication-Token") String token) {
    List<Customer> customerList = (List<Customer>) customerRepository.findAllById(customerIdList);
    customerRepository.deleteAll(customerList);
    return customerList;
  }

  @GetMapping("/{customerId}")
  public Customer getCustomer(@PathVariable("customerId") int customerId, 
		  @RequestHeader("Authentication-Token") String token) {
    return customerRepository.findById(customerId).get();
  }

  @PutMapping("/{customerId}")
  public Customer updateCustomer(@PathVariable("customerId") int customerId,
      @RequestBody Customer customer, @RequestHeader("Authentication-Token") String token) throws BookerServiceException {
    if(customerId != customer.getCustomerId()) {
      throw new BookerServiceException("Id is not the same with the object id");
    }
    if (!customerRepository.findById(customer.getCustomerId()).isPresent()) {
      throw new BookerServiceException("Customers should exist first");
    }
    customer.setCustomerId(customerId);
    return customerRepository.save(customer);
  }

  @DeleteMapping("/{customerId}")
  public Customer deleteCustomer(@PathVariable("customerId") int customerId,
		  @RequestHeader("Authentication-Token") String token) {
    Customer customer = customerRepository.findById(customerId).get();
    customerRepository.delete(customer);
    return customer;
  }
}
