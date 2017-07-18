package com.epam.server;

import org.testng.annotations.Test;
import com.epam.server.Server;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Test Class {@link ServerTest}.
 * <P>
 * Test Class ServerTest is for testing multithreading requets to server.
 */
public class ServerTest {
	private Server server;

	@BeforeClass
	public void beforeClass() {
		server = new Server(1);
		server.serverStart();
	}

	@AfterClass
	public void afterClass() {
		server.serverStop();
		server = null;
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
	@Test(groups = {
			"parallelRequests" }, dataProvider = "parallelRequests", dataProviderClass = DataProviderServerTest.class)
	public void tst_multiThreadingRequestsTest(Integer userIp, String request) {
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
	@Test(groups = {
			"sequentialRequests" }, dataProvider = "sequentialRequests", dataProviderClass = DataProviderServerTest.class)
	public void tst_sequentialRequestsTest(String expected, Integer userIp, String request) {
		Assert.assertEquals(server.takeRequest(userIp, request), expected);
	}
}
