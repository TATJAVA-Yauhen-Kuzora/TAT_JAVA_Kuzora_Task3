package com.epam.library.server;

import org.testng.annotations.Test;

import com.epam.server.Server;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class ServerTest {
	private Server server = new Server(1);

	@BeforeClass
	public void beforeMethod() {
		server.serverStart();
	}

	@AfterClass
	public void afterMethod() {
		server.serverStop();
	}

	/**
	 * Test method multiThreadingTest.
	 * 
	 * Method runs parallel threads and try to execute all of it as competing
	 * threads. Positive result for this test will be fact of executing all
	 * commands, in that case it does not matter result of each other.
	 * 
	 * @param userIp
	 *            unique user ip
	 * @param request
	 *            Strnog line with command and parameters
	 */
	@Test(dataProvider = "parallelRequests", dataProviderClass = DataProviderServerTest.class)
	public void multiThreadingTest(Integer userIp, String request) {
		System.out.println(userIp + "____" + server.takeRequest(userIp, request));
	}

	/**
	 * Test method sequentialRequestsTest.
	 * 
	 * Method runs sequential threads and try to execute all of it.
	 * 
	 * @param userIp
	 *            unique user ip
	 * @param request
	 *            Strnog line with command and parameters
	 * @param expected
	 *            Strnog line as expected result of executing
	 */
	@Test(dataProvider = "sequentialRequests", dataProviderClass = DataProviderServerTest.class)
	public void sequentialRequestsTest(String expected, Integer userIp, String request) {
		// System.out.println(userIp + "____" + server.takeRequest(userIp,
		// request));
		Assert.assertEquals(server.takeRequest(userIp, request), expected);
	}
}
