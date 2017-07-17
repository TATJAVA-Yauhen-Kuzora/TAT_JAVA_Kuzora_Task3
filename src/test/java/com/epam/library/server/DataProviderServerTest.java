package com.epam.library.server;

import org.testng.annotations.DataProvider;

public class DataProviderServerTest {
	@DataProvider(parallel = true)
	public Object[][] positiveLogin() { //
		return new Object[][] { //
				new Object[] { 123, "Login|super|12345" }, //
				new Object[] { 124, "Login|supe|12345" }, //
				new Object[] { 1256, "Login|super|12345" }, //
				new Object[] { 1256, "View_all_users" }, //
				new Object[] { 1256, "View_all_books" }, //
				new Object[] { 128, "Login|delavega|12345" }, //
		};
	}
}
