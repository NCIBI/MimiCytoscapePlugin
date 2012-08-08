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
 
package org.ncibi.cytoscape.mimi.parser;


import java.net.URL;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.net.InetSocketAddress;
import java.net.InetAddress;

import org.ncibi.cytoscape.mimi.plugin.HttpException;

/**
 * remove bad character from xml
 *
 * @author Jing Gao
 *
 * @author Alex Ade
 * Added throws clause for improved error handling and reporting.
 * Changed network code to nio to support interruptable threading.
 */
public class GetXMLFromMiMI {
	private String xml="";
	public GetXMLFromMiMI(String url) throws Exception {
		try{
	    	   URL MiMI = new URL(url);
		       //get rid of new line sign
				/*
			   InputStream in=MiMI.openStream();
			   InputStreamReader inr=new InputStreamReader(in); 					
			   BufferedReader bfr=new BufferedReader(inr);			   
			   String inputLine;		   
			   while ((inputLine = bfr.readLine()) != null)
				     xml+=inputLine;
			   in.close();
			   inr.close();
			   bfr.close();
				*/

				String host = MiMI.getHost();
				int port = (MiMI.getPort() == -1) ? 80 : MiMI.getPort();

				Charset charset = Charset.forName("ISO-8859-1");
				CharsetEncoder encoder = charset.newEncoder();
				SocketChannel sc = SocketChannel.open(new InetSocketAddress(InetAddress.getByName(host), port));

				if (!sc.isConnected()) throw new ClosedChannelException();

				sc.write(encoder.encode(CharBuffer.wrap("GET " + MiMI.toString() + " HTTP/1.0\r\n\r\n")));

				ByteBuffer buf = ByteBuffer.allocateDirect(1024);

				CharsetDecoder decoder = charset.newDecoder();
				while(sc.read(buf) != -1) {
					buf.flip();
					xml += decoder.decode(buf).toString();
					buf.clear();
				}
				//System.out.println("xml is "+xml);
				sc.close();

				int code = getResponseCode(xml);

				if (code != 200) throw new HttpException("Response Code " + code);

				int index = xml.indexOf("<?xml");
				if (index != -1) {
					xml = xml.substring(index);
				} else {
					xml = "";
					//xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><results></results>";
				}
		}
	    //catch(Exception e){System.err.println(e.toString()); }   		
	    catch(Exception e){throw e; }   		
	}

	private int getResponseCode(String xml) throws NumberFormatException {
		if (xml.startsWith("HTTP/1.")) {
			int idx = xml.indexOf(' ');
			if (idx > 0) {
				try {
					int rcode = Integer.parseInt(xml.substring(idx+1, idx+4));
					return rcode;
				} catch(NumberFormatException e) {
					throw e;
				}
			}
		}

		return -1;
	}

    public String getXML(){
    	return this.xml;
    }
}
