package cloud.computer.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println("Spring版本号：" + SpringVersion.getVersion());
		System.out.println("SpringBoot版本号：" + SpringBootVersion.getVersion());
		System.out.println();
		SpringApplication.run(BackendApplication.class, args);
	}

}
