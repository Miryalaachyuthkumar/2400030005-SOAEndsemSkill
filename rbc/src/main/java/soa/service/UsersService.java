package soa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import soa.model.Users;
import soa.repo.UsersRepo;

@Service
public class UsersService {

	UsersRepo UR;
	JWTService JWT;
	
	public UsersService(UsersRepo UR,JWTService JWT) {
		this.JWT=JWT;
		this.UR=UR;
	}
	
	public Object validateUser(String username , String password) {
		Map<String, Object> response=new HashMap<>();
		try {
			Users user = UR.validate(username, password);
			 if(user == null)
			 throw new Exception("Invalid user!");

			 Object token = JWT.generateToken(
				        user.getUsername(),
				        user.getRole().toUpperCase()
				);
			 response.put("status", "success");
			 response.put("message", "Login successful");
			 response.put("token", token);
		}catch(Exception e){
			response.put("Status", "error");
			response.put("message", e.getMessage());
		}
		return response;
	}
	
}
