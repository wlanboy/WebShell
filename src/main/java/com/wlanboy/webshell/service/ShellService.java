package com.wlanboy.webshell.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ShellService {

	public String callCommand(String command) throws IOException, InterruptedException {
		String[] parts = command.trim().split("\\s+");

		ProcessBuilder pb = new ProcessBuilder(parts);
		pb.redirectErrorStream(true);
		Process p = pb.start();

		String output;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
			output = reader.lines().collect(Collectors.joining("\n"));
		}

		p.waitFor();
		return output;
	}
}
