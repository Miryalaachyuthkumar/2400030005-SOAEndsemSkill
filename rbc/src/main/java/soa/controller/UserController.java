package soa.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import soa.service.UsersService;

@RestController
@RequestMapping("/user")
public class UserController {

	UsersService US;
	
	public UserController(UsersService US) {
		this.US=US;
	}
	@PostMapping("login")
	public Object login(@RequestBody Map<String, String> data) {
		return US.validateUser(data.get("username"),
				data.get("password"));
	}
}
