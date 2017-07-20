package com.epam.server;

import org.testng.annotations.DataProvider;

public class DataProviderServerTest {
	/**
	 * Dataprovider parallelRequests.
	 * 
	 * @return Object[][] with parallel requests from different user ip and also
	 *         parallel requests from one user ip
	 */
	@DataProvider(parallel = true)
	public Object[][] parallelRequests() { //
		return new Object[][] { //
				new Object[] { 123, "Login|super|12345" }, //
				new Object[] { 124, "Login|supe|12345" }, //
				new Object[] { 1256, "Login|super|12345" }, //
				new Object[] { 1256, "View_all_users" }, //
				new Object[] { 1256, "View_all_books" }, //
				new Object[] { 128, "Login|delavega|12345" }, //
				new Object[] { 128, "Update_user_info|Eugene|Kuzora|superProgger|4|12345" }, //
		};
	}

	/**
	 * Dataprovider sequentialRequests.
	 * 
	 * @return Object[][] with sequential requests from different user ip and
	 *         also sequential requests from one user ip
	 */
	@DataProvider(parallel = false)
	public Object[][] sequentialRequests() { //
		return new Object[][] { //
				new Object[] { "Eugene      Kuzora      /  super       /  SuperAdmin", 301, "Login|super|12345" }, //
				new Object[] { "Wrong login or password.", 304, "Login|supe|12345" }, //
				new Object[] { "Eugene      Kuzora      /  super       /  SuperAdmin", 305, "Login|super|12345" }, //
				new Object[] { "[Eugene      Kuzora      /  superProgger  /  Banned    , "
						+ "Eugene2     Kuzora2     /  user        /  Banned    , "
						+ "Eugene      Kuzora      /  moder       /  Banned    , "
						+ "delavega    delavega    /  delavega1   /  Banned    , "
						+ "Ivan        Baranov     /  LOH         /  Banned    , "
						+ "super1      super1      /  super1      /  User      , "
						+ "Java        Bolik       /  PIZDECLOGIN  /  Admin     , "
						+ "Eugene      Kuzora      /  super       /  SuperAdmin]", 301, "View_all_users" }, //
				new Object[] { "[Head First Java  /  Kathy Sierra  /  Availiable, "
						+ "Java For Testers  /  Alan Richardson  /  Availiable, "
						+ "SpeakOut  /  Steve Oakes  /  Availiable, "
						+ "SpeakOut2  /  Steve Oukes and CO  /  Availiable]", 305, "View_all_books" }, //
				new Object[] { "Accaunt has been banned.", 306, "Login|delavega1|12345" }, //
				new Object[] { "Illegal command for current user.", 306,
						"Update_user_info|Eugene|Kuzora|superProgger|4|12345" }, //
				new Object[] { "Eugene      Kuzora      /  superProgger  /  Banned    ", 301,
						"Update_user_info|Eugene|Kuzora|superProgger|4|12345" }, //

		};
	}
}
