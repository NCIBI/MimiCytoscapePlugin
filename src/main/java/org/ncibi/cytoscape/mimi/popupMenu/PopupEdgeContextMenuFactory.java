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

import org.cytoscape.application.swing.CyEdgeViewContextMenuFactory;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.model.CyEdge;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.ncibi.cytoscape.mimi.plugin.CyActivator;
import org.ncibi.cytoscape.mimi.plugin.QueryBioNlp;
import org.ncibi.cytoscape.mimi.ui.AnnoEditor;
import org.ncibi.cytoscape.mimi.ui.AnnoLogin;
import org.ncibi.cytoscape.mimi.ui.ViewPubAnno;
import org.ncibi.cytoscape.mimi.util.dynamicXpr.DynamicExpression;


/** 
 * @author JingGao/PopupEdgeContextMenuListener
 * @date Feb 20, 2007
 */
public class PopupEdgeContextMenuFactory implements CyEdgeViewContextMenuFactory{

	public CyMenuItem createMenuItem(final CyNetworkView networkView, final View<CyEdge> edgeView) {
		//add mimi plugin menu
        JMenu menu=new JMenu("MiMI Plugin");
        
        //add BioNlp to edge context menu
       // JMenu BioNLPMenu=new JMenu("BioNLP");
       // mimipluginMenu.add(BioNLPMenu);
        //JMenuItem jmiAboutNLP=new JMenuItem("About BioNLP");
        JMenuItem jmiDoNLP=new JMenuItem("BioNLP");
        menu.add(jmiDoNLP);
        //BioNLPMenu.add(jmiDoNLP);
        //BioNLPMenu.add(jmiAboutNLP);	        
        
       /* jmiAboutNLP.addActionListener(new ActionListener(){
    		public 	void actionPerformed(ActionEvent e){
    			
    			BareBonesBrowserLaunch.openURL(MiMIPlugIn.BIONLPURL);
    		}
    	});*/
        
        jmiDoNLP.addActionListener(new ActionListener(){
    		public 	void actionPerformed(ActionEvent e){
    			CyEdge edge = edgeView.getModel(); 
    			new QueryBioNlp(edge);
    		}
    	});
       
        	//add edge annotation to edge context menu
        	JMenu AnnotMenu = new JMenu("User Annotation");
        	menu.add(AnnotMenu);
        	JMenuItem jmiPubAnno= new JMenuItem("View Public Annotation");
        	JMenuItem jmiEdgeAnno = new JMenuItem ("Add Your Annotation");         
        	AnnotMenu.add(jmiPubAnno);
        	AnnotMenu.add(jmiEdgeAnno);
        	jmiPubAnno.addActionListener(new ActionListener(){
        		public 	void actionPerformed(ActionEvent e){        			
        			CyEdge edge = edgeView.getModel(); 
        			new ViewPubAnno(edge);        			
        		}
        	});
        	jmiEdgeAnno.addActionListener(new ActionListener(){
        		public 	void actionPerformed(ActionEvent e){        			
        			CyEdge edge = edgeView.getModel(); 
        			//System.out.println("click node is"+cynode.getIdentifier());
        			if (CyActivator.currentUserID.equals("0"))
        				new AnnoLogin(edge);
        			else 
        				new AnnoEditor(edge,CyActivator.currentUserID);
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










