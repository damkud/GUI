package GUI;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TwitterGUI extends ParentGUI {

	private GridLayout mainLayout= new GridLayout(2, true);
	private GridData gridData;
	private Label idLabel, strategyLabel, relationLabel, iconLabel;
	private static Text idText;
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
			      "logo_twit.jpg"));

		iconLabel= new Label(parent,SWT.ABORT);
		iconLabel.setText("Twitter");
		iconLabel.setImage(image);
		
		strategyLabel= new Label(parent, SWT.NULL);
		strategyLabel.setText("Set Download Strategy");
		
		font = new Font(parent.getDisplay(), new FontData("Arial", 11, SWT.ABORT));
		fontBord= new Font(parent.getDisplay(), new FontData("Arial", 9, SWT.BOLD));
		
		strategyLabel.setFont(font);
		strategyLabel.setLayoutData(gridData);

		strategyCombo= new Combo(parent, SWT.NULL);
		strategyCombo.setText("Choose strategy");
		strategyCombo.add("DFS");
		strategyCombo.add("BFS");
		strategyCombo.add("keywords");
		strategyCombo.setLayoutData(gridData);

		relationLabel= new Label(parent, SWT.NULL);
		relationLabel.setText("Set Download Relation");
		relationLabel.setFont(font);
		relationLabel.setLayoutData(gridData);

		relationCombo= new Combo(parent, SWT.NULL);
		relationCombo.setText("Choose relation");
		relationCombo.add("FOLLOWS");
		relationCombo.add("FOLLOWED_BY");
		relationCombo.add("REPLIES_TO");
		relationCombo.add("MENTIONS");
		relationCombo.add("HAS_TWEETS");
		relationCombo.add("RETWEETS"); 
		relationCombo.setLayoutData(gridData);
		
		idLabel= new Label(parent, SWT.NULL);
		idLabel.setText("ID: ");
		idLabel.setFont(fontBord);
		
		idText= new Text(parent, SWT.CENTER);
		
		parent.setLayout(mainLayout);
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
	
	
		
}
