package com.epam.server;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class {@link Server}.
 * <P>
 * Class Server is managing class for multithrreading logic.
 * <P>
 * <i>This Class is a member of the {@link com.epam.server} package.</i>
 */
public class Server {
	/**
	 * Unique server port.
	 */
	private int port;
	/**
	 * Field of Thread Type with anonymous Class inside which implements
	 * interface {@link Runnable}.
	 */
	private Thread serverRun;
	/**
	 * Boolean flag for managing serverRun Thread. (For stopping and starting
	 * server.)
	 */
	private boolean flagServerRunning = false;
	/**
	 * {@link ConcurrentHashMap} with connection inside for each user (thier
	 * id).
	 */
	private ConcurrentHashMap<Integer, ClientListener> connections = new ConcurrentHashMap<>();
	/**
	 * {@link ConcurrentHashMap} with unique requests, which were being taken
	 * during all working time of server.(key - result of
	 * {@link #getUniqueRequestId(int)} method, value - request as String line).
	 */
	private ConcurrentHashMap<Integer, String> requests = new ConcurrentHashMap<>();
	/**
	 * {@link ConcurrentHashMap} with unique responses, which were being taken
	 * during all working time of server.(key - result of
	 * {@link #getUniqueRequestId(int)} method, value - request as object of
	 * {@link Future} type).
	 */
	private ConcurrentHashMap<Integer, Future<String>> responses = new ConcurrentHashMap<>();
	/**
	 * {@link ConcurrentLinkedQueue} requestIds queue exists for right executing
	 * sequence.
	 */
	private ConcurrentLinkedQueue<Integer> requestIds = new ConcurrentLinkedQueue<Integer>();
	/**
	 * {@link ConcurrentHashMap} userIps links unique requestId and owner of
	 * this requset (userIp).
	 */
	private ConcurrentHashMap<Integer, Integer> userIps = new ConcurrentHashMap<>();
	private ExecutorService pool = Executors.newCachedThreadPool();

	public Server(int port) {
		this.port = port;
		serverRun = new Thread(new Runnable() {
			@Override
			public void run() {
				Integer requestId;
				while (flagServerRunning || !requests.isEmpty()) {
					requestId = requestIds.peek();
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

	/**
	 * Method serverStart.
	 * 
	 * Starting sever,this method changes {@link #flagServerRunning} on true.
	 * 
	 * @return state of {@link #flagServerRunning}.
	 */
	public boolean serverStart() {
		flagServerRunning = true;
		serverRun.start();
		return flagServerRunning;
	}

	/**
	 * Method serverStop.
	 * 
	 * Stopping sever,this method changes {@link #flagServerRunning} on false.
	 * 
	 * @return state of {@link #flagServerRunning}.
	 */
	public boolean serverStop() {
		flagServerRunning = false;
		return flagServerRunning;
	}

	/**
	 * Method takeRequest.
	 * 
	 * This method takes user ip and request, creates new {@link ClientListener}
	 * object it it necessary
	 * 
	 * @param userIp
	 *            user ip
	 * @param request
	 *            String line with command
	 * @return response as result on each request
	 */
	public String takeRequest(Integer userIp, String request) {
		Integer requestId = getUniqueRequestId(userIp);
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
		return sendResponse(requestId);
	}

	/**
	 * Private method sendResponse.
	 * 
	 * This method has logic for sending responses on each requests with waiting
	 * firsts.
	 * 
	 * @param requestId
	 *            unique request id
	 * 
	 * @return response as result on each request
	 */
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
			return e.getMessage();
		}
		return ret;
	}

	/**
	 * Private method getUniqueRequestId.
	 * 
	 * This method exists for getting unique request id for each one.
	 * 
	 * @param userIp
	 *            unique user id
	 * 
	 * @return unique Integer value
	 */
	private Integer getUniqueRequestId(int userIp) {
		Date date = new Date();
		Integer ret = (userIp * (int) date.getTime() * (int) (Math.random() * 1000));
		return ret;
	}
}
