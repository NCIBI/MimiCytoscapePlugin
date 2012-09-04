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
import java.awt.Paint;

import org.cytoscape.view.presentation.property.BasicVisualLexicon;
import org.cytoscape.view.presentation.property.NodeShapeVisualProperty;
import org.cytoscape.view.presentation.property.values.NodeShape;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.ncibi.cytoscape.mimi.plugin.QueryMiMI;


/** 
 * @author Jing Gao/MiMIVisualStyleFactory
 * @date Feb 8, 2007
 * @updated on Agu 21, 2007 removed all deprecated classes and methods to conform to cytoscape v2.5.0 
 */
public class MiMIVisualStyleBuilder {
	public static final String MiMI_VS = "MiMI";   
    public static final String NODE_CTRL_ATT="Node Color";
    public static final String USERANNOT_CTRL="Gene.userAnnot";
    
    private VisualStyleFactory visualStyleFactory;
    private VisualMappingFunctionFactory discreteMappingFactory;
    private VisualMappingFunctionFactory passthroughMappingFactory;
    
    public MiMIVisualStyleBuilder(VisualStyleFactory visualStyleFactory, VisualMappingFunctionFactory discreteMappingFactory,
    		VisualMappingFunctionFactory passthroughMappingFactory) {
    	this.visualStyleFactory = visualStyleFactory;
    	this.discreteMappingFactory = discreteMappingFactory;
    	this.passthroughMappingFactory = passthroughMappingFactory;
    }
    
    public VisualStyle createVisualStyle(){
    	VisualStyle style = visualStyleFactory.createVisualStyle(MiMI_VS);
    	
    	//set defaults
    	style.setDefaultValue(BasicVisualLexicon.NODE_FILL_COLOR, new Color(204,255,204));
    	style.setDefaultValue(BasicVisualLexicon.NODE_SHAPE, NodeShapeVisualProperty.ELLIPSE);
    	style.setDefaultValue(BasicVisualLexicon.NODE_SIZE, 35.0);
    	style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_PAINT, new Color(0,0,0));
    	style.setDefaultValue(BasicVisualLexicon.NODE_BORDER_WIDTH, 1.0);
    	style.setDefaultValue(BasicVisualLexicon.EDGE_PAINT, new Color(0,0,0));
    	style.setDefaultValue(BasicVisualLexicon.NETWORK_BACKGROUND_PAINT, Color.white);
    	
        //use Gene Name as node label     
        String nodeLabel="Gene.name";
        PassthroughMapping<String, String> nodeLabelMapping = (PassthroughMapping<String, String>) passthroughMappingFactory
				.createVisualMappingFunction(nodeLabel, String.class, BasicVisualLexicon.NODE_LABEL);
        style.addVisualMappingFunction(nodeLabelMapping);
        
        //use Gene Name as Toolkit
        PassthroughMapping<String, String> nodeTooltipMapping = (PassthroughMapping<String, String>) passthroughMappingFactory
				.createVisualMappingFunction(nodeLabel, String.class, BasicVisualLexicon.NODE_TOOLTIP);
        style.addVisualMappingFunction(nodeTooltipMapping);
       
        //set node color, shape and Size depending on "Node Color" value
		DiscreteMapping<Integer, Paint> nodeColorMapping = (DiscreteMapping<Integer, Paint>) discreteMappingFactory
				.createVisualMappingFunction(NODE_CTRL_ATT, Integer.class, BasicVisualLexicon.NODE_FILL_COLOR);
        
        nodeColorMapping.putMapValue(QueryMiMI.SEEDNODE, new Color(209,217,197));
        nodeColorMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, new Color(255,153,153));// 255,153,153
        nodeColorMapping.putMapValue(QueryMiMI.EXPANDNODE, new Color(255,153,0));
        nodeColorMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, new Color(204,204,255));     
        style.addVisualMappingFunction(nodeColorMapping);
        
        //Node shape
        DiscreteMapping<Integer, NodeShape> nodeShapeMapping = (DiscreteMapping<Integer, NodeShape>) discreteMappingFactory
				.createVisualMappingFunction(NODE_CTRL_ATT, Integer.class, BasicVisualLexicon.NODE_SHAPE);
        nodeShapeMapping.putMapValue(QueryMiMI.SEEDNODE, NodeShapeVisualProperty.DIAMOND);
        nodeShapeMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, NodeShapeVisualProperty.ELLIPSE);  
        nodeShapeMapping.putMapValue(QueryMiMI.EXPANDNODE, NodeShapeVisualProperty.TRIANGLE); 
        nodeShapeMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, NodeShapeVisualProperty.ELLIPSE);         
        style.addVisualMappingFunction(nodeShapeMapping);
        
        //Node size
        DiscreteMapping<Integer, Double> nodeSizeMapping = (DiscreteMapping<Integer, Double>) discreteMappingFactory
				.createVisualMappingFunction(NODE_CTRL_ATT, Integer.class, BasicVisualLexicon.NODE_SIZE);

        nodeSizeMapping.putMapValue(QueryMiMI.SEEDNODE, 60.0);
        nodeSizeMapping.putMapValue(QueryMiMI.SEEDNEIGHBOR, 35.0);  
        nodeSizeMapping.putMapValue(QueryMiMI.EXPANDNODE, 55.0); 
        nodeSizeMapping.putMapValue(QueryMiMI.EXPANDNEIGHBOR, 35.0); 
        style.addVisualMappingFunction(nodeSizeMapping);
        
        //Node border color control by attribute "UserAnnot"
        DiscreteMapping<Boolean, Paint> nodeBorderColorMapping = (DiscreteMapping<Boolean, Paint>) discreteMappingFactory
				.createVisualMappingFunction(USERANNOT_CTRL, Boolean.class, BasicVisualLexicon.NODE_BORDER_PAINT);
        nodeBorderColorMapping.putMapValue(false, new Color(0,0,0)); 
        nodeBorderColorMapping.putMapValue(true, new Color(0,255,0));             
        style.addVisualMappingFunction(nodeBorderColorMapping);
        
        ////Node line width control by attribute "UserAnnot"    
        DiscreteMapping<Boolean, Double> nodeBorderWidthMapping = (DiscreteMapping<Boolean, Double>) discreteMappingFactory
				.createVisualMappingFunction(USERANNOT_CTRL, Boolean.class, BasicVisualLexicon.NODE_BORDER_WIDTH);
        nodeBorderWidthMapping.putMapValue(false, 1.0); 
        nodeBorderWidthMapping.putMapValue(true, 2.0);             
        style.addVisualMappingFunction(nodeBorderWidthMapping);
        
        //edge color
        DiscreteMapping<Boolean, Paint> edgeColorMapping = (DiscreteMapping<Boolean, Paint>) discreteMappingFactory
				.createVisualMappingFunction("Interaction.userAnnot", Boolean.class, BasicVisualLexicon.EDGE_PAINT);
        edgeColorMapping.putMapValue(false, new Color(0,0,0)); 
        edgeColorMapping.putMapValue(true, new Color(0,255,0));                    
        style.addVisualMappingFunction(edgeColorMapping);
        
        //create MiMI visual style        
        return style;
    }

}
