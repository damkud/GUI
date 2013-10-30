package GUI;

import org.eclipse.e4.core.di.annotations.Execute;

@SuppressWarnings("restriction")
public class ServiceChang {
	
	@Execute
	public void execute() {
		//twit
		ServicesGUI.setService(2);
		WhatService.setService(Services.TWITTER);
	}
}
