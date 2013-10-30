package GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class YoutubeGUI extends ParentGUI {

	private GridLayout mainLayout= new GridLayout(2, false);
	private GridData gridData;
	private Label idLabel,strategyLabel, relationLabel, iconLabel;
	private static Text idText;
	private static Button vButton, uButton;
	private Font font, fontBord;
	private Combo strategyCombo, relationCombo;
	private Composite parent;
	private Image image;
	
	public void createControls(Composite parent) {
	
		this.parent= parent;
		
		mainLayout.horizontalSpacing = 5;
		mainLayout.verticalSpacing = 34;
		mainLayout.marginBottom = 7;
		mainLayout.marginTop = 7;

		gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = GridData.FILL;
		
		image= new Image(parent.getDisplay(),TwitterGUI.class.getResourceAsStream(
			      "ytLogo.png"));

		iconLabel= new Label(parent,SWT.ABORT);
		iconLabel.setText("Youtube");
		iconLabel.setImage(image);
		
		strategyLabel= new Label(parent, SWT.NULL);
		strategyLabel.setText("Set Download Strategy");
		
		font = new Font(parent.getDisplay(), new FontData("Arial", 11, SWT.ABORT));
		fontBord= new Font(parent.getDisplay(), new FontData("Arial", 9, SWT.BOLD));
		
		initLabelCombo(parent);
		
		uButton= new Button(parent, SWT.RADIO);
		uButton.setText("user");
		uButton.setSelection(true);
		
		vButton= new Button(parent, SWT.RADIO);
		vButton.setText("video");
		
		idLabel= new Label(parent, SWT.NULL);
		idLabel.setText("ID: ");
		idLabel.setFont(fontBord);
		
		idText= new Text(parent, SWT.CENTER);
		
		parent.setLayout(mainLayout);

	}

	private void initLabelCombo(Composite parent) {
		strategyLabel.setFont(font);
		strategyLabel.setLayoutData(gridData);
		
		strategyCombo= new Combo(parent, SWT.NULL);
		strategyCombo.setText("Choose strategy");
		strategyCombo.add("DFS");
		strategyCombo.add("BFS");
		strategyCombo.setLayoutData(gridData);

		relationLabel= new Label(parent, SWT.NULL);
		relationLabel.setText("Set Download Relation");
		relationLabel.setFont(font);
		relationLabel.setLayoutData(gridData);

		relationCombo= new Combo(parent, SWT.NULL);
		relationCombo.setText("Choose relation");
		relationCombo.add("UPLOADED");
		relationCombo.add("HAS_COMMENTS");
		relationCombo.add("REPLIES_TO");
		relationCombo.setLayoutData(gridData);
	}
	
	public void setVisible(boolean f){
			
		if(f){
			if(strategyLabel.isDisposed())
				createControls(parent);
		}
		else{
			strategyLabel.dispose();
			relationLabel.dispose();
			strategyCombo.dispose();
			relationCombo.dispose();
			iconLabel.dispose();
			idLabel.dispose();
			vButton.dispose();
			uButton.dispose();
			idText.dispose();
		}
	}

	public static String getIdText() {
		return idText.getText();
	}

	public static void setIdText(String idText1) {
		idText.setText(idText1);
	}

	public Combo getStrategyCombo() {
		return strategyCombo;
	}

	public void setStrategyCombo(Combo strategyCombo) {
		this.strategyCombo = strategyCombo;
	}

	public Combo getRelationCombo() {
		return relationCombo;
	}

	public void setRelationCombo(Combo relationCombo) {
		this.relationCombo = relationCombo;
	}

	public static boolean getvButton() {
		return vButton.getSelection();
	}

	public static void setvButton(boolean vButton1) {
		vButton.setSelection(vButton1);
	}

	public static boolean getuButton() {
		return uButton.getSelection();
	}

	public static void setuButton(boolean uButton1) {
		uButton.setSelection(uButton1);
	}
	
	
}
