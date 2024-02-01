package br.com.recharge.auth.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.recharge.auth.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadDataService {

	private final UserService uService;

	public String load() {
		log.info("Starting Load Users");
		buildUsersToLoad().stream().forEach(user -> uService.save(user));
		log.info("Finish Load Users");
		return "Done";
	}

	private List<User> buildUsersToLoad() {
		List<User> users = new ArrayList<>();
		users.add(User.builder().login("teste").password("teste").build());
		users.add(User.builder().login("teste1").password("teste1").build());
		users.add(User.builder().login("teste2").password("teste2").build());

		return users;
	}

}
