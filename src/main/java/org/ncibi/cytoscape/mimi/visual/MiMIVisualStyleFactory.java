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
 
package org.ncibi.cytoscape.mimi.visual;

import java.awt.Color;

import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;

import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;


/** 
 * @author Jing Gao/MiMIVisualStyleFactory
 * @date Feb 8, 2007
 * @updated on Agu 21, 2007 removed all deprecated classes and methods to conform to cytoscape v2.5.0 
 */
@SuppressWarnings("serial")
public class MiMIVisualStyleFactory {
	public static final String MiMI_VS = "MiMI";   
    public static final String NODE_CTRL_ATT="Node Color";
    public static final String USERANNOT_CTRL="Gene.userAnnot";
    
    public static VisualStyle createVisualStyle(){
        //VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
        NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator(); 
        EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
        GlobalAppearanceCalculator gac = new GlobalAppearanceCalculator();
       
        //use Gene Name as node label     
        String nodeLabel="Gene.name";       
        PassThroughMapping ptm = new PassThroughMapping(new String(), nodeLabel);          
        Calculator nlc = new BasicCalculator(nodeLabel, ptm, VisualPropertyType.NODE_LABEL ); 
        nodeAppCalc.setCalculator(nlc);
        
        //use Gene Name as Toolkit          
        Calculator nlc1 = new BasicCalculator(nodeLabel, ptm, VisualPropertyType.NODE_TOOLTIP ); 
        nodeAppCalc.setCalculator(nlc1);
       
        //set node color, shape and Size depending on "Node Color" value
        DiscreteMapping nodeColorMapping =new DiscreteMapping(new Color(255,153,153),NODE_CTRL_ATT,ObjectMapping.NODE_MAPPING);
        //DiscreteMapping nodeColorMapping =new DiscreteMapping(new Color(204,255,204),NODE_CTRL_ATT,ObjectMapping.NODE_MAPPING);
        nodeColorMapping.putMapValue(QueryMiMI.SEEDNODE, new Color(209,217,197));
        nodeColorMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, new Color(255,153,153));// 255,153,153
        //nodeColorMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, new Color(204,255,204));//(209,217,197));
        nodeColorMapping.putMapValue(QueryMiMI.EXPANDNODE, new Color(255,153,0));
        nodeColorMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, new Color(204,204,255));       
        Calculator nodeColorCalculator=new BasicCalculator("Source Node Calculator",nodeColorMapping,VisualPropertyType.NODE_FILL_COLOR); 
        nodeAppCalc.setCalculator(nodeColorCalculator);        
        
        //Node shape
        DiscreteMapping disMapping = new DiscreteMapping(
        		NodeShape.ELLIPSE,NODE_CTRL_ATT,ObjectMapping.NODE_MAPPING);
        disMapping.putMapValue(QueryMiMI.SEEDNODE, NodeShape.DIAMOND);
        disMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, NodeShape.ELLIPSE);  
        disMapping.putMapValue(QueryMiMI.EXPANDNODE, NodeShape.TRIANGLE); 
        disMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, NodeShape.ELLIPSE);         
        Calculator shapeCalculator = new BasicCalculator("MiMI Shape Calculator",disMapping,VisualPropertyType.NODE_SHAPE);        
        nodeAppCalc.setCalculator(shapeCalculator);       
        
        //Node size
        DiscreteMapping nodesizeMapping = new DiscreteMapping(35.0
        		,NODE_CTRL_ATT,ObjectMapping.NODE_MAPPING);
        nodesizeMapping.putMapValue(QueryMiMI.SEEDNODE, 60.0);
        nodesizeMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, 35.0);  
        nodesizeMapping.putMapValue(QueryMiMI.EXPANDNODE, 55.0); 
        nodesizeMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, 35.0); 
        Calculator nodesizeCalculator = new BasicCalculator("MiMI node size Calculator",nodesizeMapping,VisualPropertyType.NODE_SIZE);        
        nodeAppCalc.setCalculator(nodesizeCalculator);  
        
        //Node label color control by attribute "UserAnnot"
        /*DiscreteMapping nodeLabelColorMapping =new DiscreteMapping(new Color(0,0,0),USERANNOT_CTRL,ObjectMapping.NODE_MAPPING);
        nodeLabelColorMapping.putMapValue(false, new Color(0,0,0)); 
        nodeLabelColorMapping.putMapValue(true, new Color(255,255,255));             
        Calculator nodeLabelColorCalculator=new BasicCalculator("User Annot Attribute",nodeLabelColorMapping,VisualPropertyType.NODE_LABEL_COLOR); 
        nodeAppCalc.setCalculator(nodeLabelColorCalculator);*/
        
        //Node border color control by attribute "UserAnnot"    
        DiscreteMapping nodeBorderColorMapping =new DiscreteMapping(new Color(0,0,0),USERANNOT_CTRL,ObjectMapping.NODE_MAPPING);
        nodeBorderColorMapping.putMapValue(false, new Color(0,0,0)); 
        nodeBorderColorMapping.putMapValue(true, new Color(0,255,0));             
        Calculator nodeBorderColorCalculator=new BasicCalculator("User Annot Attribute",nodeBorderColorMapping,VisualPropertyType.NODE_BORDER_COLOR); 
        nodeAppCalc.setCalculator(nodeBorderColorCalculator);
        
        ////Node line width control by attribute "UserAnnot"    
        DiscreteMapping nodeLineMapping =new DiscreteMapping(new Integer(1),USERANNOT_CTRL,ObjectMapping.NODE_MAPPING);
        nodeLineMapping.putMapValue(false, new Integer(1)); 
        nodeLineMapping.putMapValue(true, new Integer(2));             
        Calculator nodeLineCalculator=new BasicCalculator("User Annot Attribute",nodeLineMapping,VisualPropertyType.NODE_LINE_WIDTH); 
        nodeAppCalc.setCalculator(nodeLineCalculator);
        
        //edge color 
        DiscreteMapping edgeColorMapping =new DiscreteMapping(new Color(0,0,0),"Interaction.userAnnot",ObjectMapping.EDGE_MAPPING);
        edgeColorMapping.putMapValue(false, new Color(0,0,0)); 
        edgeColorMapping.putMapValue(true, new Color(0,255,0));                    
        Calculator edgeColorCalculator=new BasicCalculator("User Edge Annot Attribute",edgeColorMapping,VisualPropertyType.EDGE_COLOR); 
        edgeAppCalc.setCalculator(edgeColorCalculator);
        
       
        //set global background
        gac.setDefaultBackgroundColor(Color.white);
        
        //create MiMI visual style        
        VisualStyle visualStyle = new VisualStyle(MiMI_VS, nodeAppCalc, edgeAppCalc,gac);
        return visualStyle;
    }

}
