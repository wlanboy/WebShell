package com.wlanboy.webshell.service;

import java.io.IOException;

/** The program does not exist, is not executable or could not be started for another reason. */
public class CommandStartException extends RuntimeException {

	public CommandStartException(String program, IOException cause) {
		super("cannot start '" + program + "': " + rootMessage(cause), cause);
	}

	/** Drops the "Cannot run program ... (in directory ...)" prefix, which repeats the program name. */
	private static String rootMessage(IOException e) {
		Throwable root = e.getCause() != null ? e.getCause() : e;
		return root.getMessage() != null ? root.getMessage().strip() : "unknown error";
	}
}
