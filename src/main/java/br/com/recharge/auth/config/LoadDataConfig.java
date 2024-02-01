package br.com.recharge.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.recharge.auth.services.LoadDataService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LoadDataConfig {

	private final LoadDataService ldService;

	@Bean
	String load() {
		return ldService.load();
	}

}
