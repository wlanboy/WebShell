package com.wlanboy.webshell.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class ShellService {

	private Runtime runtime;
	private Process p;
	
	public String callCommand(String command) throws IOException, InterruptedException {
		String output = null;
		
		StringBuilder buffer = new StringBuilder();

		if (runtime == null) {
			runtime = Runtime.getRuntime();
		}
		p = runtime.exec(new String[]{command});
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

		String line = "";
		while ((line = reader.readLine()) != null) {
			buffer.append(line + "\n");
		}

		output = buffer.toString();
		
		return output;
	}
}
