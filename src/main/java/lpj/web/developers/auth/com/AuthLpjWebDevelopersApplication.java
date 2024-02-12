package lpj.web.developers.auth.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "lpj.web.developers.auth.com")
public class AuthLpjWebDevelopersApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthLpjWebDevelopersApplication.class, args);
	}

}
