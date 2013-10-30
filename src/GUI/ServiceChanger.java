package GUI;

import org.eclipse.e4.core.di.annotations.Execute;

@SuppressWarnings("restriction")
public class ServiceChanger {
	

	@Execute
	public void execute() {
		//yt
		ServicesGUI.setService(1);
		WhatService.setService(Services.YOUTUBE);
	}
}
