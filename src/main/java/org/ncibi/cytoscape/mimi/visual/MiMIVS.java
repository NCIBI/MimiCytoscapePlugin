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
import cytoscape.Cytoscape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.*;



/** 
 * @author Jing Gao/SetMiMIVS
 * @date Feb 8, 2007
 * @Update on Aug 18 to conform to Cytsocape v2.5.0
 */
public class MiMIVS {
	public static Calculator oldmiminodecolorC;
	public static Calculator oldmiminodelabelcolorC;
	
	public static void SetMiMIVS(){		
		// Set MiMI visual style
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog cc = vmm.getCalculatorCatalog(); 		
		VisualStyle mimivs = cc.getVisualStyle("MiMI");
		
		if(mimivs == null){					
			mimivs = MiMIVisualStyleBuilder.createVisualStyle();
			cc.addVisualStyle(mimivs);
			oldmiminodecolorC=mimivs.getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_FILL_COLOR);
			oldmiminodelabelcolorC=mimivs.getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_LABEL_COLOR);
		} 		
	}

}