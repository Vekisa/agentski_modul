package com.example.agentski_modul;

import controler.SecuredServerController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AgentskiModulApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentskiModulApplication.class, args);
		SecuredServerController s=new SecuredServerController();
		s.secured();
	}

}
