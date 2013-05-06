package org.ncibi.cytoscape.mimi.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.ncibi.cytoscape.mimi.CyActivator;

@SuppressWarnings("serial")
public class MiMILegend extends JPanel implements CytoPanelComponent {

	public MiMILegend(){
		Icon icon = new ImageIcon(CyActivator.class.getResource("legend4.png"));
		JLabel l=new JLabel (icon);
		this.add(l);
	}

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.WEST;
	}

	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "MiMI Legend";
	}

}
