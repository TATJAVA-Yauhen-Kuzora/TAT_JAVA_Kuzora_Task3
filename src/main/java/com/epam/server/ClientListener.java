package com.epam.server;

import java.util.concurrent.Callable;
import com.epam.library.command.exception.CommandException;
import com.epam.library.guicontroller.Controller;

/**
 * Class {@link ClientListener}.
 * <P>
 * Class ClientListener implements {@link Callable} for realization
 * multithreading logic.
 * <P>
 * <i>This Class is a member of the {@link com.epam.server} package.</i>
 */
public class ClientListener implements Callable<String> {
	/**
	 * Current user request in each Thread.
	 */
	private String request = null;
	/**
	 * For synchronizing work with one user (same user id, many request from
	 * it).
	 */
	private volatile boolean requestExecuteFlag = true;
	/**
	 * Each unique user will get his controller, with possibility to save
	 * session.
	 */
	private Controller controller;

	/**
	 * Method call.
	 * 
	 * @return Future<String> object as response on user request
	 */
	@Override
	public String call() {
		if (controller == null) {
			controller = new Controller();
		}
		if (request != null) {
			try {
				String response = controller.executeTask(request).toString();
				request = null;
				requestExecuteFlag = true;
				return response;
			} catch (CommandException e) {
				request = null;
				requestExecuteFlag = true;
				return e.getMessage();
			}
		}
		return "This String will never be sent to user. Just hot fix.";
	}

	/**
	 * Method addRequest.
	 * 
	 * Synchronized method using simple {@link #requestExecuteFlag} boolean
	 * field.
	 * 
	 * @param request
	 *            String line with command
	 */
	public synchronized void addRequest(String request) {
		while (!requestExecuteFlag) {
		}
		this.request = request;
		requestExecuteFlag = false;
	}
}
