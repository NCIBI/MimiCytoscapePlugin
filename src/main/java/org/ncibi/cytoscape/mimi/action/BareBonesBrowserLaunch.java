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

/////////////////////////////////////////////////////////
//Bare Bones Browser Launch                          //
//Version 1.5 (December 10, 2005)                    //
//By Dem Pilafian                                    //
//Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//Example Usage:                                     //
// String url = "http://www.centerkey.com/";       //
// BareBonesBrowserLaunch.openURL(url);            //
//Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

import java.lang.reflect.Method;

import javax.swing.JOptionPane;
@SuppressWarnings("serial")

public class BareBonesBrowserLaunch {

private static final String errMsg = "Error attempting to launch web browser";

public static void openURL(String url) {
  String osName = System.getProperty("os.name");
  try {
     if (osName.startsWith("Mac OS")) {
        Class fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL = fileMgr.getDeclaredMethod("openURL",
           new Class[] {String.class});
        openURL.invoke(null, new Object[] {url});
        }
     else if (osName.startsWith("Windows"))
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
     else { //assume Unix or Linux
        String[] browsers = {
           "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
        String browser = null;
        for (int count = 0; count < browsers.length && browser == null; count++)
           if (Runtime.getRuntime().exec(
                 new String[] {"which", browsers[count]}).waitFor() == 0)
              browser = browsers[count];
        if (browser == null)
           throw new Exception("Could not find web browser");
        else
           Runtime.getRuntime().exec(new String[] {browser, url});
        }
     }
  catch (Exception e) {
     JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
     }
  }

}

