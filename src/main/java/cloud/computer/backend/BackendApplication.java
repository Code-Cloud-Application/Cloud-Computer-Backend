package cloud.computer.backend;

import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.SpringVersion;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		System.out.println("Spring版本号：" + SpringVersion.getVersion());
		System.out.println("SpringBoot版本号：" + SpringBootVersion.getVersion());
		System.out.println();
		SpringApplication.run(BackendApplication.class, args);
	}

}
