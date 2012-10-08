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
  
package org.ncibi.cytoscape.mimi.popupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.ncibi.cytoscape.mimi.plugin.MiMIState;
import org.ncibi.cytoscape.mimi.plugin.MiMIURL;
import org.ncibi.cytoscape.mimi.ui.AnnotationEditor;
import org.ncibi.cytoscape.mimi.ui.AnnotationLogin;
import org.ncibi.cytoscape.mimi.ui.ViewPublicAnnotation;
import org.ncibi.cytoscape.mimi.util.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.mimi.util.dynamicXpr.DynamicExpression;

/**
 * @author JingGao/PopupNodeContextMenuFactory
 * @date Feb 20, 2007
 */
public class PopupNodeContextMenuFactory implements CyNodeViewContextMenuFactory
{

	public CyMenuItem createMenuItem(final CyNetworkView networkView, final View<CyNode> nodeView) {
		JMenu menu = new JMenu("MiMI Plugin");

        JMenu AnnotMenu = new JMenu("User Annotation");
        menu.add(AnnotMenu);
        JMenuItem jmiPubAnno = new JMenuItem("View Public Annotation");
        JMenuItem jmiNodeAnno = new JMenuItem("Add Your Annotation");
        AnnotMenu.add(jmiPubAnno);
        AnnotMenu.add(jmiNodeAnno);
        jmiPubAnno.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode cynode = nodeView.getModel();
                new ViewPublicAnnotation(cynode);
            }
        });
        jmiNodeAnno.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode cynode = nodeView.getModel();
                // System.out.println("click node is"+cynode.getIdentifier());
                if (MiMIState.currentUserID.equals("0"))
                    new AnnotationLogin(cynode);
                else
                    new AnnotationEditor(cynode, MiMIState.currentUserID);
            }
        });

        // add ncibi linkout
        JMenu linkoutncibi = new JMenu("LinkOut NCIBI");
        menu.add(linkoutncibi);
        // add Gene2Mesh to context menu
        JMenuItem jmiGene2Mesh = new JMenuItem("Gene2Mesh");
        linkoutncibi.add(jmiGene2Mesh);
        jmiGene2Mesh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode node = nodeView.getModel();
                CyNetwork network = networkView.getModel();
                String term = network.getRow(node).get("Gene.name",String.class);
                String taxid = network.getRow(node).get("Gene.taxid", String.class);
                // get selected nodes
                /*
                 * Iterator<CyNode> nodeSet=
                 * Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();
                 * while (nodeSet.hasNext()){ CyNode sltNode=nodeSet.next();
                 * term +=
                 * Cytoscape.getNodeAttributes().getStringAttribute(sltNode
                 * .getIdentifier(), "Gene.name"); }
                 */

                BareBonesBrowserLaunch.openURL(MiMIURL.GENE2MESH + "index.php?view=simple&qtype=gene"
                        + "&term=" + term + "&taxid=" + taxid);
            }
        });

        // add mimi node link context menu
        JMenuItem jmiNodeMiMI = new JMenuItem("MiMIWeb");
        linkoutncibi.add(jmiNodeMiMI);
        jmiNodeMiMI.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode node = nodeView.getModel();
                CyNetwork network = networkView.getModel();
                String mimiNodeUrl = MiMIURL.MIMINODELINK + "?geneid=" + network.getRow(node).get(CyNetwork.NAME, String.class);
                BareBonesBrowserLaunch.openURL(mimiNodeUrl);
            }
        });
        
        //integrate dynamicXpr 
    	JMenuItem jmiNodeDyXpr=new JMenuItem("Dynamic Expression");
    	menu.add(jmiNodeDyXpr);
    	jmiNodeDyXpr.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			new DynamicExpression();
    		}
    	}); 
    	
    	return new CyMenuItem(menu,1.0f);
	}
}
