package com.wlanboy.webshell;

import java.nio.file.Path;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

/**
 * Settings for command execution.
 *
 * @param workdir   working directory of every command
 * @param timeout   commands running longer are killed together with their child processes
 * @param maxOutput output beyond this size is discarded and the command is killed
 */
@ConfigurationProperties("webshell")
public record WebshellProperties(
		@DefaultValue("/tmp") Path workdir,
		@DefaultValue("30s") Duration timeout,
		@DefaultValue("1MB") DataSize maxOutput) {

	public WebshellProperties {
		if (timeout.isNegative() || timeout.isZero()) {
			throw new IllegalArgumentException("webshell.timeout must be positive");
		}
		if (maxOutput.toBytes() <= 0 || maxOutput.toBytes() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("webshell.max-output must be between 1 byte and 2 GB");
		}
	}
}
