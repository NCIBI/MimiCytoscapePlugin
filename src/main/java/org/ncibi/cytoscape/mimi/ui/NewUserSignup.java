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
 
package org.ncibi.cytoscape.mimi.ui;

/** 
 * New user sign up panel
 */
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.cytoscape.io.util.StreamUtil;
import org.ncibi.cytoscape.mimi.MiMIURL;
import org.ncibi.cytoscape.mimi.util.MD5;

@SuppressWarnings("serial")
public class NewUserSignup extends JFrame implements ActionListener{	
	private JLabel lname;
	private JLabel lemail;
	private JLabel lorg;
	private JLabel ltil;
	private JLabel lpwd;
	private JLabel lpwd1;
	private JTextField tname;
	private JTextField temail;
	private JTextField torg;
	private JTextField ttil;
	private JPasswordField tpwd;
	private JPasswordField tpwd1;
	private StreamUtil streamUtil;
	
	public NewUserSignup(JFrame frame, StreamUtil streamUtil){
		super("Sign up");
		this.streamUtil = streamUtil;
		Container cPane = getContentPane(); 
		//Create and populate the panel.
		int row=6;
        JPanel p0 = new JPanel(new SpringLayout());
        lname = new JLabel("Name: ",JLabel.TRAILING);
        p0.add(lname);
        tname = new JTextField(30);
        p0.add(tname);
        lemail = new JLabel("Email: ",JLabel.TRAILING);
        p0.add(lemail);
        temail = new JTextField(30);
        p0.add(temail);       
        lorg = new JLabel("Organization: ",JLabel.TRAILING);
        p0.add(lorg);
        torg = new JTextField(30);
        p0.add(torg);
        ltil = new JLabel("Title: ",JLabel.TRAILING);
        p0.add(ltil);
        ttil = new JTextField(30);
        p0.add(ttil);
        lpwd = new JLabel("Password: ",JLabel.TRAILING);
        p0.add(lpwd);
        tpwd = new JPasswordField(30);
        p0.add(tpwd);
        lpwd1 = new JLabel("Verify Password: ",JLabel.TRAILING);
        p0.add(lpwd1);
        tpwd1 = new JPasswordField(30);
        p0.add(tpwd1);
      
        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p0,
                                        row, 2, //rows, cols
                                        6, 6,        //initX, initY
                                        6, 6);       //xPad, yPad
        
        JPanel p1=new JPanel();
        JButton submit= new JButton("Submit");
        submit.addActionListener(this);
        JButton cancel=new JButton("Cancel");
        cancel.addActionListener(this);
        p1.add(submit);
        p1.add(cancel);

        JPanel p=new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));        
		p.setBorder(new EmptyBorder(0,8,0,8));
		p.add(p0);
		p.add(p1);        
		cPane.add(p);		
		pack();
        setVisible(true);
        setLocationRelativeTo(frame);
		
	}
	public void actionPerformed(ActionEvent e){	
		if(e.getActionCommand().equals("Cancel")){
			setVisible(false);
		}
		if(e.getActionCommand().equals("Submit")){
			//System.out.println("name is ["+tname.getText()+"]");
			if (tname.getText().trim().length()==0)
				JOptionPane.showMessageDialog(this,"Please enter Name");	
			else if (temail.getText().trim().length()==0)
				JOptionPane.showMessageDialog(this,"Please enter Email");
			else if (tpwd.getPassword().length==0)
				JOptionPane.showMessageDialog(this,"Please enter Password");
			else if (!String.valueOf(tpwd.getPassword()).equals(String.valueOf(tpwd1.getPassword())))
				JOptionPane.showMessageDialog(this,"Passwords do not match");
			else {
				//add new user to database 
				try{
					String name=URLEncoder.encode(tname.getText(),"UTF-8");
					String email=URLEncoder.encode(temail.getText(),"UTF-8");
					String org=URLEncoder.encode(torg.getText(),"UTF-8");
					String til=URLEncoder.encode(ttil.getText(),"UTF-8");
					String pwd =  String.valueOf(tpwd.getPassword());
					pwd =URLEncoder.encode(pwd,"UTF-8");				
					String urlstr=MiMIURL.NEWUSERURL;
					String query="NAME="+name+"&PWD="+pwd+"&EMAIL="+email+"&ORG="+org+"&TIL="+til;
					URLConnection uc = streamUtil.getURLConnection(new URL(urlstr));
					uc.setDoOutput(true);	
					OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
					wr.write(query);
					wr.flush();	
					// Get the response
					BufferedReader rd = new BufferedReader(new InputStreamReader(uc.getInputStream()));	
					wr.close();	
					String inputLine1;				
					if ((inputLine1 = rd.readLine()) != null){
						setVisible(false);
						if ((!inputLine1.equals("")) && (!inputLine1.equals(" "))){
							if (inputLine1.equals("-1"))
								JOptionPane.showMessageDialog(this,"The eamil you entered already exist.\nIf you forgot your password, click \"Forgot Password\" button to get your password ");
							else{//call php file to validate log in email
								//get md5 hash
								String md5hash=new MD5(email).getmd5hash();	
								String urlstr1=MiMIURL.VALIDATEEMAIL+"?EMAIL="+email+"&PWD="+pwd+"&MD5HASH="+md5hash;
								URLConnection uc1 = streamUtil.getURLConnection(new URL(urlstr));
								uc1.setDoOutput(true);	
								OutputStreamWriter wr1 = new OutputStreamWriter(uc.getOutputStream());
								wr1.write(query);
								wr1.flush();	
								// Get the response
								BufferedReader rd1 = new BufferedReader(new InputStreamReader(uc.getInputStream()));	
								wr.close();	
								
								String inputLine2;				
								if ((inputLine2 = rd1.readLine()) != null){
								setVisible(false);
								//System.out.println("["+inputLine2+"]");
								if (inputLine2.equals("1"))
									JOptionPane.showMessageDialog(this,"We just sent you email with a link.\nPlease click the link to activate your account and then log in");
								if (inputLine2.equals("0"))
									JOptionPane.showMessageDialog(this,"Invalid Email Address.\n");
								if (inputLine2.equals("-1"))
									JOptionPane.showMessageDialog(this,"New user is not added to Database\n");
								}
								rd1.close();								
							}							
						}
					    }
						rd.close();
					}
					catch (Exception ee){
						//System.out.println(ee);
					}
			}
			
		}
	
	}
	

}
