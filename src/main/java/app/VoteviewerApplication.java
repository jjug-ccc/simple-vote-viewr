package app;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class VoteviewerApplication {
	@Component
	static class JerseryConfig extends ResourceConfig {
		public JerseryConfig() {
			packages(true, "app");
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(VoteviewerApplication.class, args);
	}
}
