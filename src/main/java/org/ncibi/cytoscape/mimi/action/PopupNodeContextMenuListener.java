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
  
package org.ncibi.cytoscape.mimi.action;

import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.ui.AnnoEditor;
import org.ncibi.cytoscape.mimi.ui.AnnoLogin;
import org.ncibi.cytoscape.mimi.ui.ViewPubAnno;
import org.ncibi.cytoscape.mimi.util.dynamicXpr.DynamicExpression;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import ding.view.NodeContextMenuListener;

/**
 * @author JingGao/PopupNodeContextMenuListener
 * @date Feb 20, 2007
 */
public class PopupNodeContextMenuListener implements NodeContextMenuListener
{
    private NodeView thecynodeview;
    public static String genepairs = "";

    public PopupNodeContextMenuListener()
    {

    }

    public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu)
    {
        thecynodeview = nodeView;
        if (menu == null)
        {
            menu = new JPopupMenu();
        }
        // add popup menu
        JMenu mimipluginMenu = new JMenu("MiMI Plugin");
        menu.add(mimipluginMenu);

        JMenuItem jmiDoNLP = new JMenuItem("BioNLP");
        jmiDoNLP.setEnabled(false);
        mimipluginMenu.add(jmiDoNLP);

        JMenu AnnotMenu = new JMenu("User Annotation");
        mimipluginMenu.add(AnnotMenu);
        JMenuItem jmiPubAnno = new JMenuItem("View Public Annotation");
        JMenuItem jmiNodeAnno = new JMenuItem("Add Your Annotation");
        AnnotMenu.add(jmiPubAnno);
        AnnotMenu.add(jmiNodeAnno);
        jmiPubAnno.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode cynode = (CyNode) thecynodeview.getNode();
                new ViewPubAnno(cynode);
            }
        });
        jmiNodeAnno.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode cynode = (CyNode) thecynodeview.getNode();
                // System.out.println("click node is"+cynode.getIdentifier());
                if (MiMIPlugin.currentUserID.equals("0"))
                    new AnnoLogin(cynode);
                else
                    new AnnoEditor(cynode, MiMIPlugin.currentUserID);
            }
        });

        // add ncibi linkout
        JMenu linkoutncibi = new JMenu("LinkOut NCIBI");
        mimipluginMenu.add(linkoutncibi);
        // add Gene2Mesh to context menu
        JMenuItem jmiGene2Mesh = new JMenuItem("Gene2Mesh");
        linkoutncibi.add(jmiGene2Mesh);
        jmiGene2Mesh.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                CyNode cynode = (CyNode) thecynodeview.getNode();
                String term = Cytoscape.getNodeAttributes().getStringAttribute(cynode.getIdentifier(),
                        "Gene.name");
                String taxid = Cytoscape.getNodeAttributes().getStringAttribute(cynode.getIdentifier(),
                        "Gene.taxid");
                // get selected nodes
                /*
                 * Iterator<CyNode> nodeSet=
                 * Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();
                 * while (nodeSet.hasNext()){ CyNode sltNode=nodeSet.next();
                 * term +=
                 * Cytoscape.getNodeAttributes().getStringAttribute(sltNode
                 * .getIdentifier(), "Gene.name"); }
                 */

                BareBonesBrowserLaunch.openURL(MiMIPlugin.GENE2MESH + "index.php?view=simple&qtype=gene"
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
                CyNode cynode = (CyNode) thecynodeview.getNode();
                String mimiNodeUrl = MiMIPlugin.MIMINODELINK + "?geneid=" + cynode.getIdentifier();
                BareBonesBrowserLaunch.openURL(mimiNodeUrl);
            }
        });
        
        //integrate dynamicXpr 
    	JMenuItem jmiNodeDyXpr=new JMenuItem("Dynamic Expression");
    	mimipluginMenu.add(jmiNodeDyXpr);
    	jmiNodeDyXpr.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent ae){         			
    			new DynamicExpression();
    		}
    	}); 

    }
}
