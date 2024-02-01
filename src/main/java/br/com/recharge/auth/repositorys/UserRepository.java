package br.com.recharge.auth.repositorys;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.recharge.auth.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByLoginAndPassword(String login, String password);

}