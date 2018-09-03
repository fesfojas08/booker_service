package pw.io.booker.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Authentication {
	@OneToOne(mappedBy="username")
	@Id
	private Customer user;
	private LocalDate loginDate;
	private String token;
	
	public Customer getUser() {
		return user;
	}
	public void setUser(Customer user) {
		this.user = user;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LocalDate getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(LocalDate loginDate) {
		this.loginDate = loginDate;
	}
}
