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
 * @author jinggao/AnnoLogin
 * @date Mar 27, 2007
 */

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.model.CyIdentifiable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.swing.DialogTaskManager;
import org.ncibi.cytoscape.mimi.MiMIURL;
import org.ncibi.cytoscape.mimi.task.ApplyVisualStyleAndLayoutTaskFactory;

@SuppressWarnings("serial")
public class AnnotationLogin extends JFrame implements ActionListener {
	private CyIdentifiable obj;
	private CyNetwork network;
	private CyNetworkView view;
	private DialogTaskManager dialogTaskManager;
	private ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory;
	private JFrame frame;
	private StreamUtil streamUtil;
	private JTextField temail;
	private JPasswordField tpaswd;	
	private String NEWUSER="-1";
	private String WRONGPWD="-2";
	private String FAKEEMAIL="-3";

	public AnnotationLogin(CyIdentifiable obj, CyNetwork network, CyNetworkView view, DialogTaskManager dialogTaskManager, ApplyVisualStyleAndLayoutTaskFactory vslTaskFactory, JFrame frame, StreamUtil streamUtil){
		super("Sign in");
		this.obj=obj;
		this.network=network;
		this.view = view;
		this.dialogTaskManager = dialogTaskManager;
		this.vslTaskFactory = vslTaskFactory;
		this.frame = frame;
		this.streamUtil = streamUtil;
		Container cPane = getContentPane(); 		
		JPanel p=new JPanel();
		p.setLayout(new GridLayout(0,1));
		p.setBorder(new EmptyBorder(0,8,0,8));
		JPanel p0=new JPanel();
		JLabel llogin=new JLabel("Please enter");
		p0.add(llogin);
		JPanel p1=new JPanel ();		
		JLabel lemail=new JLabel("Your Emailadd: ",JLabel.TRAILING);
		temail=new JTextField(20);
		p1.add(lemail);
		p1.add(temail);
		JPanel p2=new JPanel();
		JLabel lpaswd=new JLabel("Your Password: ",JLabel.TRAILING);
		tpaswd=new JPasswordField (20);
		p2.add(lpaswd);
		p2.add(tpaswd);
		JPanel p3=new JPanel ();
		JButton bsubmitbutton=new JButton("Submit");
		JButton bcancelbutton=new JButton("Cancel");
		bsubmitbutton.addActionListener(this );
		bcancelbutton.addActionListener(this);
		p3.add(bsubmitbutton);
		p3.add(bcancelbutton);
		JPanel p4=new JPanel();
		JButton newuser= new JButton("New User");
		JButton forgotpw= new JButton("Forgot Password");
		newuser.addActionListener(this);
		forgotpw.addActionListener(this);
		p4.add(newuser);
		p4.add(forgotpw);
		p.add(p0);
		p.add(p1);
		p.add(p2);
		p.add(p3);
		p.add(p4);
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
			String tEmail=temail.getText();			
			String tPaswd=String.valueOf(tpaswd.getPassword());

			if (tEmail !=null && !tEmail.trim().equals("")&& tPaswd !=null && !tPaswd.trim().equals("")){

				try{
					tEmail=URLEncoder.encode(tEmail,"UTF-8");
					tPaswd =URLEncoder.encode(tPaswd,"UTF-8");	
					String urlstr=MiMIURL.ANNOTEDITORLOGIN;
					String query= "EMAIL="+tEmail+"&PWD="+tPaswd;
					URLConnection uc = streamUtil.getURLConnection(new URL(urlstr));
					uc.setDoOutput(true);	
					OutputStreamWriter wr = new OutputStreamWriter(uc.getOutputStream());
					wr.write(query);
					wr.flush();	
					// Get the response
					BufferedReader rd = new BufferedReader(new InputStreamReader(uc.getInputStream()));	
					wr.close();	
					String inputLine1;				

					if ((inputLine1 = rd.readLine()) != null)
						if (inputLine1.equals(NEWUSER)){
							JOptionPane.showMessageDialog(this,"The email does not exist in our database.\nIf you are a new user, Please clcik \"New User\" button to register.\nOtherwise, please check your spelling");
						}
						else if (inputLine1.equals(WRONGPWD)){
							JOptionPane.showMessageDialog(this,"Invalid Password. Please click \"Forgot Password\" button to get your password");						
						}
						else if (inputLine1.equals(FAKEEMAIL)){
							JOptionPane.showMessageDialog(this,"Sorry, your account is not activated, you could not log in");
						}
						else{
							setVisible(false);
							if ((!inputLine1.equals("")) && (!inputLine1.equals(" "))){	
								//System.out.println(inputLine1);
								new AnnotationEditor(obj, network, view, inputLine1, dialogTaskManager, vslTaskFactory, frame, streamUtil);						

							}				
						}				
					rd.close();
				}
				catch (Exception ee){
					//System.out.println(ee);
				}
			}		
		}
		if(e.getActionCommand().equals("New User")){
			setVisible(false);
			new NewUserSignup(frame, streamUtil);
		}

		if(e.getActionCommand().equals("Forgot Password")){
			setVisible(false);
			new ForgotPassword(frame, streamUtil);

		}
	}

}
