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
 
package org.ncibi.cytoscape.mimi.plugin;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.ncibi.cytoscape.mimi.enums.QueryType;

/**
 * URLDropHandler
 *
 * TransferHandler to manage drops of URLs onto Cytoscape to initial
 * query.
 *
 * @author Alex Ade
 * @date   Thu Nov 16 14:05:11 EST 2006
 */
@SuppressWarnings("serial")
public class URLDropHandler extends TransferHandler {
	//private String protocol = "http";
	//private String host     = "developer2.ncibi.org";
	private HashMap<String, QueryType> type;

	public URLDropHandler(HashMap<String, QueryType> type) {
		this.type = type;
	}

	public boolean canImport(JComponent comp, DataFlavor[] flavors) {
		for (int i = 0; i < flavors.length; i++) {
			if (flavors[i].equals(DataFlavor.stringFlavor)) return true;
		}

		return false;
	}
	
	public boolean importData(JComponent comp, Transferable t) {
		if (!t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}

		try {
			// fix for an odd firefox bug on windows that creates
			// copy of dropped data
			String s = (String)t.getTransferData(DataFlavor.stringFlavor);
			int idx = s.indexOf('\n');
			if (idx != -1) s = s.substring(0, idx);

			//URL url=new URL((String)t.getTransferData(DataFlavor.stringFlavor));
			URL url = new URL(s);

			//if (url.getProtocol().equals(protocol) &&
			//	url.getHost().equals(host)) {

				String[] token = url.getQuery().split("=");

				if (!type.containsKey(token[0])) return false;

				if (token[1] != null && !token[1].equals("")) {
					new QueryMiMIWrapper(type.get(token[0]), token[1]);
				} else return false;
			//} else return false;

		} catch (UnsupportedFlavorException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
}
