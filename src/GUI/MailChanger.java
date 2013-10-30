package GUI;

import org.eclipse.e4.core.di.annotations.Execute;


@SuppressWarnings("restriction")
public class MailChanger {

	public static boolean isNotifi= false;
	
	@Execute
	public void execute() {

		isNotifi= !isNotifi;
		
	}

}
