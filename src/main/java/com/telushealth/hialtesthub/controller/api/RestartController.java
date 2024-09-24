package com.telushealth.hialtesthub.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.telushealth.hialtesthub.HialTestHubApplication;

@Controller
public class RestartController {

	@Autowired
	private ConfigurableApplicationContext context;

	@GetMapping("/restart")
	public String restart() {
		// Close the current application context
		context.close();

		// Restart the application
		SpringApplication.run(HialTestHubApplication.class);

		return "redirect:/";
	}
}
