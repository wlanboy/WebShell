package com.wlanboy.webshell.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wlanboy.webshell.WebshellProperties;

@Service
public class ShellService {

	/** Exit code reported for a timeout, same as coreutils {@code timeout}. */
	public static final int EXIT_TIMEOUT = 124;

	private static final Logger logger = LoggerFactory.getLogger(ShellService.class);

	/** How long to wait for remaining output after the process has ended. */
	private static final Duration OUTPUT_GRACE = Duration.ofSeconds(1);

	private final WebshellProperties properties;

	public ShellService(WebshellProperties properties) {
		this.properties = properties;
	}

	/**
	 * @param output    stdout and stderr merged, possibly cut at {@code webshell.max-output}
	 * @param exitCode  exit code of the process, {@link #EXIT_TIMEOUT} on timeout
	 * @param timedOut  the process was killed after {@code webshell.timeout}
	 * @param truncated the process was killed after exceeding {@code webshell.max-output}
	 */
	public record CommandResult(String output, int exitCode, boolean timedOut, boolean truncated) {

		public boolean successful() {
			return exitCode == 0 && !timedOut && !truncated;
		}
	}

	public CommandResult execute(String commandLine) throws InterruptedException {
		List<String> args = CommandLineParser.parse(commandLine);
		if (args.isEmpty()) {
			throw new InvalidCommandException("empty command");
		}

		Process process = start(args);
		OutputCollector output = new OutputCollector((int) properties.maxOutput().toBytes());
		Thread reader = Thread.ofVirtual()
			.name("shell-output-" + process.pid())
			.start(() -> output.drain(process.getInputStream(), () -> destroyProcessTree(process)));

		boolean finished;
		try {
			finished = process.waitFor(properties.timeout().toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			destroyProcessTree(process);
			throw e;
		}
		if (!finished) {
			destroyProcessTree(process);
		}

		// A background child that inherited stdout can keep the pipe open after the process
		// itself has ended. Do not wait for it, return what has been read so far.
		reader.join(OUTPUT_GRACE);

		if (!finished) {
			return new CommandResult(output.text(), EXIT_TIMEOUT, true, output.truncated());
		}
		return new CommandResult(output.text(), process.exitValue(), false, output.truncated());
	}

	private Process start(List<String> args) {
		try {
			return new ProcessBuilder(args)
				.directory(properties.workdir().toFile())
				.redirectErrorStream(true)
				.start();
		} catch (IOException e) {
			throw new CommandStartException(args.getFirst(), e);
		}
	}

	/** Kills the children first: once the parent is gone they are re-parented and no longer found. */
	private static void destroyProcessTree(Process process) {
		process.descendants().forEach(ProcessHandle::destroyForcibly);
		process.destroyForcibly();
		try {
			if (!process.waitFor(5, TimeUnit.SECONDS)) {
				logger.warn("process {} did not terminate after SIGKILL", process.pid());
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/** Reads the process output up to a fixed number of bytes. Thread-safe. */
	private static final class OutputCollector {

		private final int limit;
		private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		private volatile boolean truncated;

		OutputCollector(int limit) {
			this.limit = limit;
		}

		void drain(InputStream in, Runnable onLimitExceeded) {
			byte[] chunk = new byte[8192];
			try (in) {
				int n;
				while ((n = in.read(chunk)) != -1) {
					synchronized (buffer) {
						int room = limit - buffer.size();
						buffer.write(chunk, 0, Math.min(n, room));
						truncated = n > room;
					}
					if (truncated) {
						onLimitExceeded.run();
						return;
					}
				}
			} catch (IOException e) {
				// The stream is closed when the process gets killed, nothing left to read.
				logger.debug("reading process output stopped", e);
			}
		}

		String text() {
			synchronized (buffer) {
				return buffer.toString(StandardCharsets.UTF_8);
			}
		}

		boolean truncated() {
			return truncated;
		}
	}
}
