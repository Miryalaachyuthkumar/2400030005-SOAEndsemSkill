package soa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import soa.model.Users;

@Repository
public interface UsersRepo extends JpaRepository<Users, Long>{

	@Query("SELECT u FROM Users u WHERE u.username = :username AND u.password = :password")
	public Users validate(@Param("username") String username , @Param("password") String password);
	
}
