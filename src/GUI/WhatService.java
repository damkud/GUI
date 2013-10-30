package GUI;

public class WhatService {

	private static Services service = Services.YOUTUBE;

	public static void setService(Services serv) {

		service = serv;
	}

	public static Services getService() {

		return service;
	}
}
