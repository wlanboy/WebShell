package com.wlanboy.webshell.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wlanboy.webshell.service.ShellService;

@Controller
public class IndexController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	ShellService service;
	
	@RequestMapping("/")
	public String index(Model model) {

		String error = null;
		String shell = "/bin/bash";
		String command = "ls";
		String output = "";

		model.addAttribute("shell", shell);
		model.addAttribute("command", command);
		model.addAttribute("output", output);
		model.addAttribute("error", error);
		return "index";
	}
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/")
	public String upload(@RequestParam("command") String command, Model model) {
		String error = null;
		String output = "";
	    
		try {
	        output = service.callCommand(command);

		} catch (Exception ex) {
			logger.error("group", ex);
			error = ex.getMessage();
		}

		model.addAttribute("command", command);
		model.addAttribute("output", output);
		model.addAttribute("error", error);

		return "index";
	}

}
