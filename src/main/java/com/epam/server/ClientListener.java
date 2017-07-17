package com.epam.server;

import java.util.concurrent.Callable;
import com.epam.library.command.exception.CommandException;
import com.epam.library.guicontroller.Controller;

public class ClientListener implements Callable<String> {
	private String request = null;
	private Controller controller;

	@Override
	public String call() {
		if (controller == null) {
			controller = new Controller();
		}
		if (request != null) {
			try {
				String response = controller.executeTask(request).toString();
				request = null;
				return response;
			} catch (CommandException e) {
				request = null;
				return e.getMessage();
			}
		}
		return "Where is your command?";
	}

	public void addRequest(String request) {
		this.request = request;
	}
}
