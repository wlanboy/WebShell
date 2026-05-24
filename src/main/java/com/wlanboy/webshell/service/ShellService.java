package com.wlanboy.webshell.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ShellService {

	public record CommandResult(String output, int exitCode) {}

	public CommandResult callCommand(String command) throws IOException, InterruptedException {
		String[] parts = command.trim().split("\\s+");

		ProcessBuilder pb = new ProcessBuilder(parts);
		pb.directory(new File("/tmp"));
		pb.redirectErrorStream(true);
		Process p = pb.start();

		String output;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			output = reader.lines().collect(Collectors.joining("\n"));
		}

		if (!p.waitFor(30, TimeUnit.SECONDS)) {
			p.destroyForcibly();
			return new CommandResult("Timeout: command exceeded 30 seconds", 124);
		}

		return new CommandResult(output, p.exitValue());
	}
}
