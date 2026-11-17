package com.wlanboy.webshell.service;

/** The command line is empty or cannot be parsed. */
public class InvalidCommandException extends RuntimeException {

	public InvalidCommandException(String message) {
		super(message);
	}
}
