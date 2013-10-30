package GUI;


import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;

public class ServicesGUI {

	private static Composite parent;
	private static YoutubeGUI gui= new YoutubeGUI();
	private static TwitterGUI gui2= new TwitterGUI();
	
	@PostConstruct
	public void createControls(Composite parent) {

		setParent(parent);
		gui.createControls(parent);
		gui2.createControls(parent);
		gui2.setVisible(false);
	
	}

	
	public static void setService(int opt){
		
		switch(opt){
		
		case 1:
				gui.setVisible(true);
				gui2.setVisible(false);
				break;
		case 2: 			
				gui.setVisible(false);
				gui2.setVisible(true);
				break;
		default: 
				gui.setVisible(true);
				gui2.setVisible(false);
		}
		
		parent.layout();
	}

	public static Composite getParent() {
		return parent;
	}

	public static void setParent(Composite parent) {
		ServicesGUI.parent = parent;
	}
}
