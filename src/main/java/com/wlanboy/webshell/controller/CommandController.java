package com.wlanboy.webshell.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wlanboy.webshell.WebshellProperties;
import com.wlanboy.webshell.service.CommandStartException;
import com.wlanboy.webshell.service.InvalidCommandException;
import com.wlanboy.webshell.service.ShellService;
import com.wlanboy.webshell.service.ShellService.CommandResult;

@RestController
public class CommandController {

	private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

	private final ShellService service;
	private final WebshellProperties properties;

	public CommandController(ShellService service, WebshellProperties properties) {
		this.service = service;
		this.properties = properties;
	}

	/** Settings shown by the built-in {@code help} command. */
	public record Settings(String workdir, long timeoutSeconds, long maxOutputBytes) {
	}

	@GetMapping("/settings")
	public Settings settings() {
		return new Settings(
			properties.workdir().toString(),
			properties.timeout().toSeconds(),
			properties.maxOutput().toBytes());
	}

	/**
	 * Returns 200 on exit code 0, otherwise 422. The body always contains the output, followed by a
	 * note if the command was killed.
	 */
	@PostMapping(value = "/execute", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> execute(@RequestParam("command") String command) {
		CommandResult result;
		try {
			result = service.execute(command);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("interrupted");
		}

		String body = result.output();
		if (result.truncated()) {
			body += "\n[output exceeded " + properties.maxOutput().toBytes() + " bytes, command killed]";
		}
		if (result.timedOut()) {
			body += "\n[timeout after " + properties.timeout().toSeconds() + "s, command killed]";
		}
		HttpStatus status = result.successful() ? HttpStatus.OK : HttpStatus.UNPROCESSABLE_CONTENT;
		return ResponseEntity.status(status).body(body);
	}

	@ExceptionHandler(InvalidCommandException.class)
	ResponseEntity<String> invalidCommand(InvalidCommandException e) {
		return plainText(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(CommandStartException.class)
	ResponseEntity<String> commandStartFailed(CommandStartException e) {
		logger.debug("command start failed", e);
		return plainText(HttpStatus.UNPROCESSABLE_CONTENT, e.getMessage());
	}

	private static ResponseEntity<String> plainText(HttpStatus status, String body) {
		return ResponseEntity.status(status).contentType(MediaType.TEXT_PLAIN).body(body);
	}
}
