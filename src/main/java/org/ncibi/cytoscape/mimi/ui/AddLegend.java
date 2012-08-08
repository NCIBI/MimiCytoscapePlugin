/*************************************************************************
 * Copyright 2012 Regents of the University of Michigan 
 * 
 * NCIBI - The National Center for Integrative Biomedical Informatics (NCIBI)
 *         http://www.ncib.org.
 * 
 * This product includes software developed by other, see specific notes in the code. 
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or (at your option) any later version, along with the following terms:
 * 1.	You may convey a work based on this program in accordance with section 5, 
 *      provided that you retain the above notices.
 * 2.	You may convey verbatim copies of this program code as you receive it, 
 *      in any medium, provided that you retain the above notices.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details, http://www.gnu.org/licenses/.
 * 
 * This work was supported in part by National Institutes of Health Grant #U54DA021519
 *
 ******************************************************************/
 
package org.ncibi.cytoscape.mimi.ui;


/** 
 * @author jinggao/AddLegend
 * @date Jul 3, 2008
 */
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.Cytoscape;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
//import java.net.*;
import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
@SuppressWarnings("serial")
public class AddLegend {
	public AddLegend(){
		CytoPanelImp ctrlPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
		MiMILegend mimilegend = new MiMILegend(); 
		int mimiidx=ctrlPanel.indexOfComponent("MiMI Legend");
		if (mimiidx==-1)
			ctrlPanel.add("MiMI Legend",mimilegend);
		//int indexInCytoPanel= ctrlPanel.indexOfComponent("MiMI Legend");
		//ctrlPanel.setSelectedIndex(indexInCytoPanel);
	}
	
	class MiMILegend extends JPanel{
		public MiMILegend(){
			Icon icon = new ImageIcon(MiMIPlugin.class.getResource("legend4.png"));
			JLabel l=new JLabel (icon);
			this.add(l);
			
		}
		
	}

}
