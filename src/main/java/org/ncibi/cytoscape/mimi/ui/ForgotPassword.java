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
 * @author jinggao/ForgotPswd
 * @date Jul 2, 2008
 */
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URLEncoder;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.ncibi.cytoscape.mimi.plugin.MiMIURL;

@SuppressWarnings("serial")
public class ForgotPassword extends JFrame implements ActionListener {
	private JTextField t;
	public ForgotPassword(JFrame parent){	
		super("Forgot Password");
		JLabel l=new JLabel("Please enter your email: ");
		t=new JTextField(40);
		JButton bsbmt=new JButton("Submit");
		bsbmt.addActionListener(this);
		JButton bcncl=new JButton("Cancel");
		bcncl.addActionListener(this);
		JPanel p0=new JPanel();
		p0.setLayout(new BoxLayout(p0, BoxLayout.Y_AXIS));        
		//p0.setBorder(new EmptyBorder(0,8,0,8));
		p0.add(l);
		p0.add(t);
		JPanel p1=new JPanel();
		p1.add(bsbmt);
		p1.add(bcncl);
		JPanel p=new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));        
		p.setBorder(new EmptyBorder(0,8,0,8));
		p.add(p0);
		p.add(p1);
		Container cpane =getContentPane();		
		cpane.add(p);
		pack();
        setVisible(true);
        setLocationRelativeTo(parent);		
	}
	public void actionPerformed (ActionEvent e){
		if (e.getActionCommand().equals("Submit")){
			if (t.getText().trim().equals(""))
				JOptionPane.showMessageDialog(this, "Please enter your emmail");
			else {
				try{
					//System.out.println("emial is "+t.getText());
					String urlStr = MiMIURL.SENDPSWD;
					String query="EMAIL="+URLEncoder.encode(t.getText(),"UTF-8");
					URLConnect uc=new URLConnect();
					uc.doURLConnect(urlStr, query) ;
					String rslt="";
					if ((rslt=uc.getBrd().readLine())!=null)						
						if (rslt.equals("1"))
							JOptionPane.showMessageDialog(this, "Your password was sent to your email successfully");
						else if (rslt.equals("-1")) 
							JOptionPane.showMessageDialog(this, "Your email does not exist in our database.\nPlease check your spelling");
							
					setVisible(false);
				}
				catch(Exception es){
					//System.out.println(es);
				}
			}
		}
		if (e.getActionCommand().equals("Cancel"))
			setVisible(false);
	}

}
