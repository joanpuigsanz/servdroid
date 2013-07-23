/*
 * Copyright (C) 2010 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.servDroid.util.shell;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.servDroid.util.Logger;

public class ShellCommands {

	private static final String TAG = "ShellCommands";
	
	public static int EXIT_STATUS_COMMAND_NOT_FOUND = 127;
	public static int EXIT_STATUS_OK = 127;

	/**
	 * Run a command and get the output.
	 * 
	 * @param cmd
	 *            the command to run
	 * @param readExitCode
	 *            if the app should wait for the exit
	 * @return {@link CommandOutput} with the output of the command
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static CommandOutput runCommand(String[] cmd, boolean readExitCode)
			throws IOException, InterruptedException {
		// Executes the command.
		Process exec = Runtime.getRuntime().exec(cmd);

		String out = null;
		StringBuffer output = null;

		// Reads stdout.
		// NOTE: You can write to stdin of the command using
		// process.getOutputStream().
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				exec.getInputStream()));
		int read;
		char[] buffer = new char[4096];
		output = new StringBuffer();
		while ((read = reader.read(buffer)) > 0) {
			output.append(buffer, 0, read);
		}
		reader.close();
		int exitCode = -1;

		// Waits for the command to finish.
		if (readExitCode) {
			exitCode = exec.waitFor();
			if (null != output) {
				out = output.toString();
				Logger.d(TAG, "out == " + out);
			}
		}

		return new CommandOutput(out, exitCode);
	}

	/**
	 * Check if a command exist
	 * 
	 * @param cmd
	 *            the command to check (without any parameters).
	 * @return true if exist, false otherwise
	 */
	public static boolean isCommandExist(String cmd) {
		CommandOutput out = null;
		try {
			out = runCommand(new String[] { cmd }, true);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		// Logger.d(TAG, " out --> " + out);
		if (out.getExitCode() == EXIT_STATUS_COMMAND_NOT_FOUND) {
			return false;
		}
		return true;
	}

	/**
	 * Check if the iptables command exist.
	 * 
	 * @return true if iptables exist, false otherwise
	 */
	public static boolean isIptablesExist() {
		return isCommandExist("iptables");
	}

	/**
	 * Check if the device is rooted
	 * 
	 * @return true if it is rooted, false otherwise
	 */
	public static boolean isDeviceRooted() {

		try {
			if (runCommand(new String[] { "su", "-c", "id " }, true).getOutput()
					.contains("uid=0")) {
				Logger.d(TAG, "Root access alloed");
				return true;
			}
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		return false;
	}

	/**
	 * Execute the commands defined in the list as a superuser (if it is
	 * possible).
	 * 
	 * @param cmds
	 *            Commands to execute
	 * @return The output of the commands
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static String doSuCmds(List<String> cmds) throws IOException,
			InterruptedException {
		Process process = Runtime.getRuntime().exec("su");

		StringBuffer output = null;
		// Reads stdout.
		// NOTE: You can write to stdin of the command using
		// process.getOutputStream().
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		int read;
		char[] buffer = new char[4096];
		output = new StringBuffer();
		// output.append(reader.readLine());

		DataOutputStream os = new DataOutputStream(process.getOutputStream());

		for (String tmpCmd : cmds) {
			os.writeBytes(tmpCmd + "\n");
		}

		os.writeBytes("exit\n");
		os.flush();
		os.close();
		while ((read = reader.read(buffer)) > 0) {
			output.append(buffer, 0, read);
			// Logger.d(TAG, " output: "+output.toString());
		}

		process.waitFor();
		return output.toString();
	}

	/**
	 * Close the NAT ports.
	 * 
	 * @return
	 */
	public static boolean closeNatPorts() {
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("iptables -t nat -F");

		try {
			String out = doSuCmds(cmds);
			Logger.d(TAG, " output: " + out);

			// String out = runCommand(new String[] {
			// "iptables",
			// " -t nat -A PREROUTING -p tcp --dport " + dPort
			// + " -j REDIRECT --to-port " + toPort }, true);
			// Logger.d(TAG, " opening nat: " + out);
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}

	/**
	 * Open A NAT port
	 * 
	 * @param dPort
	 * @param toPort
	 * @return
	 */
	public static boolean openNatPort(int dPort, int toPort) {

		// iptables -t nat -A PREROUTING -p tcp --dport 80 -j REDIRECT --to-port
		// 65485
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add("iptables -t nat -A PREROUTING -p tcp --dport " + dPort
				+ " -j REDIRECT --to-port " + toPort);

		try {
			String out = doSuCmds(cmds);
			Logger.d(TAG, " output: " + out);

			// String out = runCommand(new String[] {
			// "iptables",
			// " -t nat -A PREROUTING -p tcp --dport " + dPort
			// + " -j REDIRECT --to-port " + toPort }, true);
			// Logger.d(TAG, " opening nat: " + out);
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		return true;
	}

}
