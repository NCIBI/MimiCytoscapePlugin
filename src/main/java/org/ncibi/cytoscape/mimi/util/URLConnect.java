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
 
package org.ncibi.cytoscape.mimi.util;

/** 
 * @author jinggao/URLConnect
 * @date Jun 28, 2008
 */
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.ncibi.cytoscape.mimi.plugin.QueryMiMIWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import cytoscape.Cytoscape;

import cytoscape.util.ProxyHandler;
public class URLConnect {
	private BufferedReader brd;
	private URLConnection conn ;
	public void doURLConnect(String urlStr, String query){
		try{
			URL url = new URL(urlStr);
			Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
			//URLConnection conn ;
			if (CytoProxyHandler == null) 
				conn = url.openConnection();
			else conn = url.openConnection(CytoProxyHandler);			
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(query);
			wr.flush();
			wr.close();	
			// Get the response	        
			brd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 			
			
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Could not connect to the remote file.\n Please check if the remote file exist"); 
			//System.out.println(e);			
		}
		
	}
	public void doURLConnect(String urlStr){
		try{
			URL url = new URL(urlStr);
			Proxy CytoProxyHandler = ProxyHandler.getProxyServer();			
			if (CytoProxyHandler == null) 
				conn = url.openConnection();
			else conn = url.openConnection(CytoProxyHandler);			
			conn.setUseCaches(false);
			conn.setDoOutput(true);				        
			brd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	 			
			
		}
		catch(Exception e){
			QueryMiMIWrapper.popupTimer.stop();
 		    QueryMiMIWrapper.dialog.setVisible(false);
 		    QueryMiMIWrapper.dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
 		    QueryMiMIWrapper.dialog.pack();
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Could not connect to the remote file.\n Please make sure the remote file exist"); 
			//System.out.println(e);			
		}
		
	}
	public BufferedReader getBrd (){
		return brd;		
	}
	public void closebrd(){
		try{
			brd.close();		
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Could not connect to the remote file.\n Please check if the remote file exist"); 
			//System.out.println(e);
		}
	}
}
