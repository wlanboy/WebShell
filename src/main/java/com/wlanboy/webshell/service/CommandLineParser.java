package com.wlanboy.webshell.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits a command line into arguments, roughly like a POSIX shell does, but without any
 * expansion: no pipes, redirects, variables or globs.
 * <ul>
 * <li>Whitespace separates arguments.</li>
 * <li>{@code 'single quotes'} keep everything literally.</li>
 * <li>{@code "double quotes"} keep everything except {@code \"} and {@code \\}.</li>
 * <li>Outside of quotes, a backslash escapes the next character.</li>
 * </ul>
 */
public final class CommandLineParser {

	private CommandLineParser() {
	}

	public static List<String> parse(String commandLine) {
		List<String> args = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean inArgument = false;
		int i = 0;
		int length = commandLine.length();

		while (i < length) {
			char c = commandLine.charAt(i);
			if (Character.isWhitespace(c)) {
				if (inArgument) {
					args.add(current.toString());
					current.setLength(0);
					inArgument = false;
				}
				i++;
			} else if (c == '\'') {
				int end = commandLine.indexOf('\'', i + 1);
				if (end < 0) {
					throw new InvalidCommandException("unclosed single quote");
				}
				current.append(commandLine, i + 1, end);
				inArgument = true;
				i = end + 1;
			} else if (c == '"') {
				i = readDoubleQuoted(commandLine, i + 1, current);
				inArgument = true;
			} else if (c == '\\') {
				if (i + 1 >= length) {
					throw new InvalidCommandException("trailing backslash");
				}
				current.append(commandLine.charAt(i + 1));
				inArgument = true;
				i += 2;
			} else {
				current.append(c);
				inArgument = true;
				i++;
			}
		}
		if (inArgument) {
			args.add(current.toString());
		}
		return args;
	}

	/** Reads up to the closing quote and returns the index after it. */
	private static int readDoubleQuoted(String commandLine, int start, StringBuilder target) {
		int i = start;
		while (i < commandLine.length()) {
			char c = commandLine.charAt(i);
			if (c == '"') {
				return i + 1;
			}
			if (c == '\\' && i + 1 < commandLine.length()
					&& (commandLine.charAt(i + 1) == '"' || commandLine.charAt(i + 1) == '\\')) {
				target.append(commandLine.charAt(i + 1));
				i += 2;
			} else {
				target.append(c);
				i++;
			}
		}
		throw new InvalidCommandException("unclosed double quote");
	}
}
