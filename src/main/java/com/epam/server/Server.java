/**
 * 
 */
package com.epam.server;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Eugene13
 *
 */
public class Server {
	private int port;
	private Thread serverRun;
	private boolean flagServerRunning = false;
	// Место хранение подключений
	private ConcurrentHashMap<Integer, ClientListener> connections = new ConcurrentHashMap<>();
	// Map of requests
	private ConcurrentHashMap<Integer, String> requests = new ConcurrentHashMap<>();
	// Map of responses
	private ConcurrentHashMap<Integer, Future<String>> responses = new ConcurrentHashMap<>();
	// Queue of ip
	private ConcurrentLinkedQueue<Integer> requestIds = new ConcurrentLinkedQueue<Integer>();

	private ConcurrentHashMap<Integer, Integer> userIps = new ConcurrentHashMap<>();
	// Thread pool
	private ExecutorService pool = Executors.newCachedThreadPool();

	public Server(int port) {
		this.port = port;
		serverRun = new Thread(new Runnable() {
			@Override
			public void run() {
				while (flagServerRunning) {
					Integer requestId = requestIds.peek();
					try {
						if ((requestId != null) & (requests.get(requestId) != null)) {
							requestId = requestIds.poll();
							ClientListener clientListener = connections.get(userIps.get(requestId));
							String request = requests.get(requestId);
							requests.remove(requestId);
							clientListener.addRequest(request);
							Future<String> future = pool.submit(clientListener);
							responses.put(requestId, future);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
	}

	public boolean serverStart() {
		flagServerRunning = true;
		serverRun.start();
		return flagServerRunning;
	}

	public boolean serverStop() {
		flagServerRunning = false;
		return flagServerRunning;
	}

	public String takeRequest(Integer userIp, String request) {
		Integer requestId = gashFuck(userIp);
		try {
			synchronized (connections) {

				if (connections.containsKey(userIp)) {
					requests.put(requestId, request);
					requestIds.add(requestId);
					userIps.put(requestId, userIp);
				} else {
					ClientListener clientListener = new ClientListener();
					connections.put(userIp, clientListener);
					requests.put(requestId, request);
					requestIds.add(requestId);
					userIps.put(requestId, userIp);
				}
			}

		} catch (Exception e) {
		}
		return sendResponse(requestId);
	}

	private synchronized String sendResponse(int userIp) {
		Future<String> response = null;
		String ret = "";
		while (response == null) {
			response = responses.get(userIp);
		}
		while (!response.isDone()) {
		}
		try {
			ret = response.get();
			responses.remove(userIp);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private synchronized Integer gashFuck(int userIp) {
		Date date = new Date();
		Integer ret = (userIp * (int) date.getTime() * (int) (Math.random() * 1000));

		System.out.println(userIp + "  " + ret);
		return ret;
	}
}
