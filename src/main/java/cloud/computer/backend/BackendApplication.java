package cloud.computer.backend;

import cloud.computer.backend.Entity.OpenStackDatabaseSyncer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableAsync
public class BackendApplication {

	private OpenStackDatabaseSyncer openStackDatabaseSyncer;

	public BackendApplication(OpenStackDatabaseSyncer openStackDatabaseSyncer) {
		this.openStackDatabaseSyncer = openStackDatabaseSyncer;
	}

	@PostConstruct
	private void init(){
		this.openStackDatabaseSyncer.sync();
	}

	public static void main(String[] args) {
		System.out.println("Spring版本号：" + SpringVersion.getVersion());
		System.out.println("SpringBoot版本号：" + SpringBootVersion.getVersion());
		System.out.println();
		SpringApplication.run(BackendApplication.class, args);

	}

}
