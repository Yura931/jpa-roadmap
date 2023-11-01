package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class DatajpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatajpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorAware() {
		// 나중에 spring security나 session 등 사용해서 사용자 넘겨주면 됨
		// 등록, 수정될 때마다 이 provider를 호출해서 createdBy, modifedBy 값을 채워 줌
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
