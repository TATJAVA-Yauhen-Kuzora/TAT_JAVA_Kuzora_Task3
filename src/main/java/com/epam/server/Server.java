package com.epam.server;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Server {
	private int port;
	private Thread serverRun;
	private boolean flagServerRunning = false;
	private ConcurrentHashMap<Integer, ClientListener> connections = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, String> requests = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, Future<String>> responses = new ConcurrentHashMap<>();
	private ConcurrentLinkedQueue<Integer> requestIds = new ConcurrentLinkedQueue<Integer>();
	private ConcurrentHashMap<Integer, Integer> userIps = new ConcurrentHashMap<>();
	private ExecutorService pool = Executors.newCachedThreadPool();

	public Server(int port) {
		this.port = port;
		serverRun = new Thread(new Runnable() {
			@Override
			public void run() {
				while (flagServerRunning || !requests.isEmpty()) {
					Integer requestId = requestIds.peek();
					try {
						if ((requestId != null) & (requests.get(requestId) != null)) {
							requestId = requestIds.poll();
							ClientListener clientListener = connections.get(userIps.get(requestId));
							String request = requests.get(requestId);
							requests.remove(requestId);
							clientListener.addRequest(request);
							userIps.remove(requestId);
							Future<String> future = pool.submit(clientListener);
							responses.put(requestId, future);
						}
					} catch (NullPointerException e) {
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
		Integer requestId = getUniqueRequestId(userIp);
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

	private String sendResponse(int requestId) {
		Future<String> response = null;
		String ret = "";
		while (response == null) {
			response = responses.get(requestId);
		}
		while (!response.isDone()) {
		}
		try {
			ret = response.get();
			responses.remove(requestId);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return ret;
	}

	private Integer getUniqueRequestId(int userIp) {
		Date date = new Date();
		Integer ret = (userIp * (int) date.getTime() * (int) (Math.random() * 1000));
		return ret;
	}
}
