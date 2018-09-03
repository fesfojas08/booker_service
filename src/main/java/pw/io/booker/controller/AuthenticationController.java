package pw.io.booker.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pw.io.booker.model.Authentication;
import pw.io.booker.model.Customer;
import pw.io.booker.repo.AuthenticationRepository;
import pw.io.booker.repo.CustomerRepository;
import pw.io.booker.util.TokenCreator;

@RestController
@RequestMapping
public class AuthenticationController {
	private AuthenticationRepository authenticationRepository;
	private CustomerRepository customerRepository;
	private TokenCreator tokenCreator;
	
	public AuthenticationController(AuthenticationRepository authenticationRepository,
			CustomerRepository customerRepository, TokenCreator tokenCreator) {
		super();
		this.authenticationRepository = authenticationRepository;
		this.customerRepository = customerRepository;
		this.tokenCreator = tokenCreator;
	}

	@PostMapping("/login")
	public String login(@RequestBody Authentication authentication) {
		String token = "";
		Optional<Customer> customer = customerRepository.findByUsernameAndPassword(
				authentication.getUser().getUsername(), 
				authentication.getUser().getPassword());
		if(!customer.isPresent()) {
			throw new RuntimeException("Invalid Credentials!");
		}
		
		authenticationRepository.deleteAll(authenticationRepository.findByUser(customer.get()));
		token = tokenCreator.encode(customer.get());
		
		Authentication loginAuth = new Authentication();
		loginAuth.setUser(customer.get());
		loginAuth.setLoginDate(LocalDate.now());
		loginAuth.setToken(token);
		authenticationRepository.save(authentication);
		return token;
	}
	
	@PostMapping("logout")
	public void logout(@RequestParam("token") String token) {
		Authentication currentAuth = authenticationRepository.findByToken(token).get();
		authenticationRepository.delete(currentAuth);
	}
}
