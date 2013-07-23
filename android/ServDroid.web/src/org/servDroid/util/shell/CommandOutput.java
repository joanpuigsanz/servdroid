package org.servDroid.util.shell;

public class CommandOutput {
	private int exitCode;
	private String output;

	public CommandOutput(String out, int exitCode) {
		this.output = out;
		this.exitCode = exitCode;
	}

	public int getExitCode() {
		return exitCode;
	}

	public String getOutput() {
		return output;
	}
}