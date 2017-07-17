package com.epam.library.server;

import org.testng.annotations.Test;

import com.epam.server.Server;

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

	@Test(dataProvider = "positiveLogin", dataProviderClass = DataProviderServerTest.class)
	public void f(Integer userIp, String request) {
		System.out.println(userIp + " " + server.takeRequest(userIp, request));
	}
}
