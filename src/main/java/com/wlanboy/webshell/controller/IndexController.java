package com.wlanboy.webshell.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wlanboy.webshell.service.ShellService;

@Controller
public class IndexController {

	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	@Autowired
	ShellService service;

	@PostMapping(value = "/execute", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> execute(@RequestParam("command") String command) {
		try {
			String output = service.callCommand(command);
			return ResponseEntity.ok(output);
		} catch (Exception ex) {
			logger.error("command execution failed", ex);
			return ResponseEntity.internalServerError().body(ex.getMessage());
		}
	}
}
