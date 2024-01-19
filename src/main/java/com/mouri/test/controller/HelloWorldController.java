package com.mouri.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {

	@RequestMapping("/public/applications")
	public String hello(Model model) {
		
		model.addAttribute("greeting", "Hello public/applications");
		
//		return "Your web Application deployed successfully";
		return "helloworld";
		
	}

}