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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.ncibi.cytoscape.mimi.action.BareBonesBrowserLaunch;
import org.ncibi.cytoscape.mimi.parser.UserAnnoAttr;
import org.ncibi.cytoscape.mimi.plugin.MiMIPlugin;
import org.ncibi.cytoscape.mimi.util.URLConnect;
import org.ncibi.cytoscape.mimi.visual.LayoutUtility;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.ProxyHandler;
import cytoscape.view.CyNetworkView;



/**
 *  
 * a node annotation editor
 * 
 * @author JingGao/AnnoEditor
 * @date Mar 6, 2007
 */
@SuppressWarnings("serial")
public class AnnoEditor extends JFrame implements ActionListener{ //,ItemListener{
	private Container cPane;
	private JTextArea content;
	private JList urlList;
	private JTextField nameText;
	private JTextField tchromosome;
	private JTextField tlocustag;		
	private JTextField tmaploc;
	private JTextField talias;	
	private JTextField ttype;
	private JTextField ttaxonomyName;
	private JTextField ttaxid;
	private JTextField tdescription;
	private JTextField tcomponent;	
	private JTextField tfunction;	
	private JTextField tprocess;		
	private JTextField tcomplex;	
	private JTextField titype;
	private JTextField tiprovenance;
	private JTextField ticomponent;
	private JTextField tifunction;
	private JTextField tiprocess;
	private JTextField tipubmed;	
	private JComboBox annotSetList;
	private JCheckBox sharebutton;
	private JPanel p;
	private JPanel p6;	
	private JTextField urlTextField;
	private ArrayList<String> urls=new ArrayList<String>();
	private ArrayList<String> annotSetNameList= new ArrayList<String>();
	private String name="";
	private String id="";
	private String alias="";
	private String chromosome="";
	private String maploc="";	
	private String locustag="";
	private String taxid="";
	private String taxonomyName="";	
	private String type="";
	private String description="";
	private String component="";
	private String function="";
    private String process="";
    private String complex=""; 
    private String itype="";
    private String iprovenance ="";
    private String icomponent="";
    private String ifunction ="";
    private String iprocess="";
    private String ipubmed="";
	private String annotSetName="";
	private String table="";    
	private String annotation="";		
	private String userID=" ";	
	private int nodeOrEdge;
	private String share;
	
	public AnnoEditor (Object obj, String userid){		
		super ("Annotating Editor");	
		userID = userid;
		MiMIPlugin.currentUserID=userID;
				
		if (obj instanceof CyNode){		
			nodeOrEdge=1;
			table="Node";
			CyNode cynode= (CyNode)obj;
			name=Cytoscape.getNodeAttributes().getStringAttribute(cynode.getIdentifier(), "Gene.name");
			id=cynode.getIdentifier();			
			getAnnotSetNameList(id,table,userID);			
			if (annotSetNameList.size()==0){			
				annotSetName="INPUT YOUR ANNOTATION NAME";
				annotSetNameList.add(annotSetName);				
				getNodeAttributes(cynode);	
				share="1";
				//initialSave();				
			}		
		}
		
		if (obj instanceof CyEdge){	
			nodeOrEdge=0;
			table="Edge";
			CyEdge cyedge= (CyEdge)obj;
			name=Cytoscape.getNodeAttributes().getStringAttribute(cyedge.getSource().getIdentifier(),"Gene.name")
						+"<=>"+Cytoscape.getNodeAttributes().getStringAttribute(cyedge.getTarget().getIdentifier(),"Gene.name");
			id =cyedge.getIdentifier();			
			getAnnotSetNameList(id,table,userID);	
			if (annotSetNameList.size()==0){			
				annotSetName="INPUT YOUR ANNOTATION NAME HERE";				
				annotSetNameList.add(annotSetName);				
				getEdgeAttributes(cyedge);		
				share="1";
				//initialSave();				
			}
		}
		share="1";
		cPane = getContentPane(); 		
		p=new JPanel();		
		p.setLayout(new GridLayout(0,1));
		p.setBorder(new EmptyBorder(0,8,0,8));
		p.setSize(480, 0);
		p6=new JPanel();
		p6.setLayout(new GridLayout(0,1));
		p6.setBorder(new EmptyBorder(0,8,0,8));
		JLabel blankLabel= new JLabel("Annotation Set Name");
		p6.add(blankLabel);			
		annotSetList=new JComboBox(annotSetNameList.toArray());		
		annotSetList.setSelectedIndex(0);
		annotSetName=(String)annotSetList.getItemAt(0);			
		if (annotSetName.equals("MiMI") )
			getContent(table,annotSetName,id,"0");
		else
			getContent(table,annotSetName,id,userID);	 	       
		annotSetList.setEditable(true);	
		annotSetList.addActionListener(this);		
		p6.add(annotSetList);
		
		JLabel idLabel =new JLabel("ID",JLabel.LEADING);			
		p.add(idLabel);
		JTextField idtext=new JTextField(id,10);
		idtext.setEditable(false);
		idLabel.setLabelFor(idtext);
		p.add(idtext);
		JLabel nameLabel =new JLabel("Name");
		p.add(nameLabel);
		nameText=new JTextField(name,10);	
		nameText.setEditable(false);
		p.add(nameText);
		
		if (obj instanceof CyNode){				
			JLabel lchromosome=new JLabel("Chromosome");
			p.add(lchromosome);
			tchromosome=new JTextField(chromosome,10);
			tchromosome.setCaretPosition(0);			
			p.add(tchromosome);
			JLabel llocustag=new JLabel("Locustag");
			p.add(llocustag);
			tlocustag=new JTextField(locustag,10);		
			p.add(tlocustag);
			JLabel lmaploc=new JLabel("Map_loc");
			p.add(lmaploc);
			tmaploc= new JTextField(maploc,10);		
			p.add(tmaploc);
			JLabel lalias=new JLabel("Other Gene Names");		
			p.add(lalias);
			talias=new JTextField(alias,10);
			talias.setCaretPosition(0);				
			p.add(talias);			
			JLabel ltype=new JLabel("Type");
			p.add(ltype);
			ttype =new JTextField(type,10);
			ttype.setCaretPosition(0);		
			p.add(ttype);			
			JLabel ltaxonomyName=new JLabel("Organism");		
			p.add(ltaxonomyName);
			ttaxonomyName=new JTextField(taxonomyName,10);
			ttaxonomyName.setCaretPosition(0);				
			p.add(ttaxonomyName);
			JLabel ltaxid=new JLabel("TaxID");
			p.add(ltaxid);
			ttaxid =new JTextField(taxid,10);
			ttaxid.setCaretPosition(0);		
			p.add(ttaxid);
			JLabel lcomponent=new JLabel("Component");
			p.add(lcomponent);
			tcomponent=new JTextField(component,10);
			tcomponent.setCaretPosition(0);			
			p.add(tcomponent);
			JLabel ldescription=new JLabel("Description");
			p.add(ldescription);
			tdescription=new JTextField(description,10);
			tdescription.setCaretPosition(0);			
			p.add(tdescription);
			JLabel lfunction=new JLabel("Function");
			p.add(lfunction);
			tfunction= new JTextField(function,10);
			tfunction.setCaretPosition(0);			
			p.add(tfunction);
			JLabel lprocess=new JLabel("Process");
			p.add(lprocess);
			tprocess =new JTextField (process,10);
			tprocess.setCaretPosition(0);		
			p.add(tprocess);
			JLabel lcomplex=new JLabel("Complex");		
			p.add(lcomplex);
			tcomplex=new JTextField(complex,10);
			tcomplex.setCaretPosition(0);				
			p.add(tcomplex);
		}
		if (obj instanceof CyEdge){
			JLabel litype=new JLabel("Type");		
			p.add(litype);
			titype=new JTextField(itype,10);
			titype.setCaretPosition(0);				
			p.add(titype);
			JLabel licomponent =new JLabel("Component");
			p.add(licomponent);
			ticomponent =new JTextField(icomponent, 10);
			ticomponent.setCaretPosition(0);
			p.add(ticomponent);
			JLabel lifunction= new JLabel ("Function");
			p.add(lifunction);
			tifunction =new JTextField(ifunction, 10);
			tifunction.setCaretPosition(0);
			p.add(tifunction);
			JLabel liprocess=new JLabel("Process");
			p.add(liprocess);
			tiprocess=new JTextField(iprocess, 10);
			tiprocess.setCaretPosition(0);
			p.add(tiprocess);
			JLabel liprovenance=new JLabel("Provenance");
			p.add(liprovenance);
			tiprovenance=new JTextField(iprovenance, 10);
			tiprovenance.setCaretPosition(0);
			p.add(tiprovenance);
			JLabel lipubmed=new JLabel("Pubmed");
			p.add(lipubmed);
			tipubmed=new JTextField(ipubmed, 10);
			tipubmed.setCaretPosition(0);
			p.add(tipubmed);
			
		}		
		
		JPanel p5= new JPanel();
		p5.setLayout(new GridLayout(0,1));	
		p5.setBorder(new EmptyBorder(0,8,0,8));
		JLabel anno=new JLabel("Add your annotation");
		anno.setToolTipText("Do not forget Save/Logout");
		p5.add(anno);	
		
		JPanel p1 =new JPanel();
		p1.setBorder(new EmptyBorder(0,8,0,8));
		p1.setLayout(new GridLayout(0,1));
		content = new JTextArea(2, 10);			
		JScrollPane scrollPane = 
		    new JScrollPane(content,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		content.setEditable(true);
		content.setText(annotation);
		p1.add(scrollPane);
		
		JPanel p3=new JPanel();
		p3.setLayout(new GridLayout(0,1));	
		p3.setBorder(new EmptyBorder(0,8,0,8));
		JLabel urlLabel= new JLabel("Add your URLs");
		urlLabel.setToolTipText("Add URLs related to selected node/edge");
		p3.add(urlLabel);	
		
		JPanel p4=new JPanel();
		p4.setLayout(new GridLayout(0,1));
		p4.setBorder(new EmptyBorder(0,8,0,8));		
		urlList=new JList();
		urlList.setListData(urls.toArray());
		urlList.setSelectionMode(
	            ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		urlList.setSelectedIndex(0);		
		urlList.addMouseListener(new TheMouseListener());
	    JScrollPane listScrollPane = new JScrollPane(urlList);
		p4.add(listScrollPane);
		
		JPanel wrapper= new JPanel();
		wrapper.setLayout(new GridLayout(0,1));
		wrapper.setBorder(new EmptyBorder(0,8,0,8));
		JPanel urlButtons=new JPanel();		
		urlTextField=new JTextField(25);		
		urlButtons.add(urlTextField);
		JButton urlAdd=new JButton("Add");
		urlAdd.addActionListener(this);
		JButton urlDelete=new JButton("Delete");
		urlDelete.addActionListener(this);
		urlButtons.add(urlAdd);
		urlButtons.add(urlDelete);		

		JPanel buttonbox=new JPanel();
		sharebutton = new JCheckBox("Share Your Annotation");
		//System.out.println("share is ["+share+"]");
		if (share.equals("1")){
			sharebutton.setSelected(true);	
			//System.out.println("share button set true");
		}
		else sharebutton.setSelected(false);
		JButton button1=new JButton("Save...");
		button1.addActionListener(this);		
		JButton button3=new JButton("Cancel");
		button3.addActionListener(this);
		JButton buttonLogout=new JButton("Log Out");
		buttonLogout.addActionListener(this);
		buttonbox.add(sharebutton);
		buttonbox.add(button1);	
		buttonbox.add(button3);
		buttonbox.add(buttonLogout);
		
		JPanel parentPanel= new JPanel();				
		parentPanel.setLayout(new BoxLayout(parentPanel,BoxLayout.Y_AXIS));	
		parentPanel.add(p6);
		parentPanel.add(p);		
		parentPanel.add(p5);
		parentPanel.add(p1);
		parentPanel.add(p3);
		parentPanel.add(p4);
		parentPanel.add(urlButtons);	
		parentPanel.add(buttonbox);			
		JScrollPane scrollCPane = 
		    new JScrollPane(parentPanel,
		                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cPane.add(scrollCPane);			
		cPane.setPreferredSize(new Dimension(480,500));
        pack();
        setVisible(true);
        setLocationRelativeTo(Cytoscape.getDesktop());
		
		
	}
	
	public void actionPerformed(ActionEvent e) {		
		if (e.getActionCommand().equals("Save...")){	
			if (!annotSetName.equals("")){
				doSaveUpdate();	
				if (nodeOrEdge==1 && sharebutton.isSelected())
					Cytoscape.getNodeAttributes().setAttribute(id, "Gene.userAnnot", true);
				if (nodeOrEdge==0 && sharebutton.isSelected())
					Cytoscape.getEdgeAttributes().setAttribute(id, "Gene.edgeUserAnnot", true);
				if (nodeOrEdge==1 && !sharebutton.isSelected()){
					UserAnnoAttr uaa1=new UserAnnoAttr();
					uaa1.getAttribute(id,"");
					if (uaa1.getGenewithPublAnnot().equals(""))
						Cytoscape.getNodeAttributes().setAttribute(id, "Gene.userAnnot", false);
				}
				if (nodeOrEdge==0 && !sharebutton.isSelected()){
					UserAnnoAttr uaa1=new UserAnnoAttr();
					uaa1.getAttribute("",id);
					if (uaa1.getEdgewithPublAnnot().equals(""))
						Cytoscape.getEdgeAttributes().setAttribute(id, "Gene.userAnnot", false);					
				}				 
				LayoutUtility.doLayout();  	
				CyNetworkView curNetView = Cytoscape.getCurrentNetworkView();
				curNetView.setVisualStyle("MiMI");
				Cytoscape.getVisualMappingManager().setNetworkView(curNetView); 
				Cytoscape.getVisualMappingManager().setVisualStyle("MiMI"); 
				curNetView.redrawGraph(false, true);
			}
			else JOptionPane.showMessageDialog(this,"Please enter annotation set name");
		}
		else if (e.getActionCommand().equals("Cancel")){
			setVisible(false);
		}
		else if (e.getActionCommand().equals("Add")){
			if (urlTextField.getText()!=null && !urlTextField.getText().equals("")) {
				urls.add(urlTextField.getText());
				urlList.setListData(urls.toArray());	
				urlTextField.setText("");
			}			
		}
		else if (e.getActionCommand().equals("Delete")){
			Object[] selectUrls=urlList.getSelectedValues();
			for (int i=0;i<selectUrls.length;i++)
				urls.remove((String)selectUrls[i]);				
			urlList.setListData(urls.toArray());					
		}
		else if (e.getActionCommand().equals("comboBoxChanged")){
			JComboBox cb = (JComboBox)e.getSource();			
			annotSetName = (String)cb.getSelectedItem();	
	        if (annotSetName !=null){
	        	//System.out.println("asn is ["+annotSetName);
	        	getContent(table,annotSetName,id,userID);
	        	content.setText(annotation);
	        	content.setCaretPosition(0);
	        	nameText.setText(name);	
	        	nameText.setCaretPosition(0);	  
	        	if (share.equals("1"))
	        		sharebutton.setSelected(true);
	        	else sharebutton.setSelected(false);  	
				
	        	urlList.setListData(urls.toArray());
	        	if (nodeOrEdge==1){//node	        		
	        		tchromosome.setText(chromosome);	 
	        		tchromosome.setCaretPosition(0);
	        		tlocustag.setText(locustag);
	        		tlocustag.setCaretPosition(0);
	        		tmaploc.setText(maploc);
	        		tmaploc.setCaretPosition(0);
	        		talias.setText(alias);
	        		talias.setCaretPosition(0);
	        		ttype.setText(type);
	        		ttype.setCaretPosition(0);
	        		ttaxonomyName.setText(taxonomyName);
	        		ttaxonomyName.setCaretPosition(0);	
	        		ttaxid.setText(taxid);
	        		ttaxid.setCaretPosition(0);
	        		tdescription.setText(description);
	        		tdescription.setCaretPosition(0);
	        		tcomponent.setText(component);	
	        		tcomponent.setCaretPosition(0);
	        		tfunction.setText(function);
	        		tfunction.setCaretPosition(0);
	        		tprocess.setText(process);
	        		tprocess.setCaretPosition(0);
	        		tcomplex.setText(complex);
	        		tcomplex.setCaretPosition(0);	        		
	        	}
	        	if (nodeOrEdge==0){//edge
	        		titype.setText(itype);
	        		titype.setCaretPosition(0);
	        		tiprovenance.setText(iprovenance);
	        		tiprovenance.setCaretPosition(0);	        		
	        		ticomponent.setText(icomponent);
	        		ticomponent.setCaretPosition(0);	    			
	        		tifunction.setText(ifunction);
	        		tifunction.setCaretPosition(0); 
	        		tiprocess.setText(iprocess);
	        		tiprocess.setCaretPosition(0);	    			
	        		tipubmed.setText(ipubmed);
	        		tipubmed.setCaretPosition(0);
	        	
	        	}
	        }
		}
		else if (e.getActionCommand().equals("Log Out")){
			setVisible(false);
			MiMIPlugin.currentUserID="0";
		}			
	}
	

	private void getContent(String table,String setName,String id, String userid){		
		try{
			urls=new ArrayList<String>();
			String setID="";
			String result="";			
			setName=URLEncoder.encode(setName,"UTF-8");
			id =URLEncoder.encode(id,"UTF-8");
			String url="http://mimiplugin.ncibi.org/dbaccess/3.2/queryMiMIAnnot_rdb.php";						
			URL ncibi_dbx=new URL(url);	
			String query="TABLE="+table+"&SETNAME="+setName+"&ID="+id+"&USERID="+userid;
			//System.out.println("get content query is "+query);
			Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
			URLConnection conn ;
			if (CytoProxyHandler == null) 
				conn = ncibi_dbx.openConnection();
			else conn = ncibi_dbx.openConnection(CytoProxyHandler);
			//URLConnection conn = ncibi_dbx.openConnection();
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(query);
			wr.flush();					
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));	
			String inputLine1;				
			if ((inputLine1 = rd.readLine()) != null)
				result =inputLine1;			
			rd.close();	
			wr.close();
			//System.out.println("get node info from database is ["+result+"]");
			//System.out.println("query is ["+query);
			if (!result.equals("")){
				
				String[] resultArray=result.split("/////");		
				//System.out.println("get cloumns "+resultArray.length);				
				if (table.equals("Node")){
					setID=resultArray[0];
					name=resultArray[2];
					chromosome=resultArray[3];
					locustag=resultArray[4];
					maploc=resultArray[5];					
					alias=resultArray[6];
					type=resultArray[7];
					taxonomyName=resultArray[8];
					taxid=resultArray[9];
					description=resultArray[10];
					component=resultArray[11];
					function=resultArray[12];
					process=resultArray[13];
					//kegg=resultArray[14];
					//pathway=resultArray[15];
					complex=resultArray[16];
					annotation=resultArray[17];
					share=resultArray[18];
								
				}
					
				if(table.equals("Edge")){
					setID=resultArray[0];
					name=resultArray[2];
				    itype=resultArray[3];
				    icomponent=resultArray[4];					
				    ifunction=resultArray[5];
				    iprocess=resultArray[6];
				    iprovenance=resultArray[7];
				    ipubmed=resultArray[8];
				    annotation=resultArray[9];
				    share=resultArray[10];					
				}		
				//System.out.println("share from getcontent is ["+share+"]");
				//get urls
				if (!setName.equals("MiMI")){
					urls=new ArrayList<String>();					
					
					String urlStr="http://mimiplugin.ncibi.org/dbaccess/3.2/queryMiMIURLs_rdb.php";
					URL javaurl = new URL(urlStr);
					//Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
					URLConnection conn1 ;
					if (CytoProxyHandler == null) 
						conn1 = javaurl.openConnection();
					else conn1 = javaurl.openConnection(CytoProxyHandler);
					//URLConnection conn1 = javaurl.openConnection();
					conn1.setUseCaches(false);
					conn1.setDoOutput(true);							
					String query1="TABLE="+table+"&SETID="+setID;
					//System.out.println("query is "+urlStr+query1) ;						
					OutputStreamWriter wr1 = new OutputStreamWriter(conn1.getOutputStream());
					wr1.write(query1);					
					wr1.flush();	 					
					// Get the response	        
					BufferedReader rd1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
					String line;
					while ((line = rd1.readLine()) != null){ 
						urls.add(line);
						//System.out.println(line);
					}
					wr1.close();			
				}
			}
		}
		catch (Exception e){
   			//System.out.println(e);
        }	
	
	}
	
	private void getAnnotSetNameList(String id, String tableName,String userid){
		try{
			id =URLEncoder.encode(id,"UTF-8");
			String urlstr=MiMIPlugin.ANNOTSETNAME;
			String query="TABLE="+tableName+"&ID="+id+"&USERID="+userid;
			URLConnect uc= new URLConnect();
			uc.doURLConnect(urlstr, query);
			String inputLine1;				
			while ((inputLine1 = uc.getBrd().readLine()) != null)
				annotSetNameList.add(inputLine1);
			uc.closebrd();			
		}
		catch (Exception e){
   			//System.out.println(e);
        }		
		
		
	}
	
	/*private void initialSave(){
		try{	
				String lid=URLEncoder.encode(id,"UTF-8");
				String annot="";		
				String annotsetname=URLEncoder.encode(annotSetName,"UTF-8");				
				String query="TABLE="+table+"&ID="+lid+"&SETNAME="+annotsetname+"&ANNOTATION="+annot+"&USERID=0&SHARE=1";	
				
				if (nodeOrEdge==1){
					
					String ename =URLEncoder.encode(name,"UTF-8");
					query +="&GENENAME="+ename;
					String ealias=URLEncoder.encode(alias,"UTF-8");
					query +="&ALIAS="+ealias;					
					String echromosome=URLEncoder.encode(chromosome,"UTF-8");
					query +="&CHROMOSOME="+echromosome;						
					String emaploc = URLEncoder.encode(maploc,"UTF-8");
					query +="&MAPLOC="+emaploc;					
					String elocustag=URLEncoder.encode(locustag,"UTF-8");
					query +="&LOCUSTAG="+elocustag;						
					String etaxid=URLEncoder.encode(taxid,"UTF-8");
					query +="&TAXID="+etaxid;				
					String etaxonomyName=URLEncoder.encode(taxonomyName,"UTF-8");
					query +="&TAXNAME="+etaxonomyName;					
					String etype=URLEncoder.encode(type,"UTF-8");
					query +="&TYPE="+etype; 					
					String edescription=URLEncoder.encode(description,"UTF-8");
					query += "&DESCRIPTION="+edescription;
					String ecomponent=URLEncoder.encode(component,"UTF-8");
					query += "&COMPONENT="+ecomponent;
					String efunction=URLEncoder.encode(function,"UTF-8");
					query += "&FUNCTION="+efunction;
					String eprocess=URLEncoder.encode(process,"UTF-8");
					query += "&PROCESS="+eprocess;	
					String ekegg=URLEncoder.encode(kegg,"UTF-8");
					query +="&KEGG="+ekegg;
					String epathway=URLEncoder.encode(pathway,"UTF-8");
					query +="&PATHWAY="+epathway;
					String ecomplex=URLEncoder.encode(complex,"UTF-8");
					query +="&COMPLEX="+ecomplex;
					
				}
				if (nodeOrEdge==0){//edge
					String ename =URLEncoder.encode(name,"UTF-8");
					query +="&INTGENENAME="+ename;
					String etype=URLEncoder.encode(itype,"UTF-8");
					query +="&TYPE="+etype; 
					String eiprovenance=URLEncoder.encode(iprovenance,"UTF-8");
					query +="&PROVENANCE="+eiprovenance;
					String eicomponent=URLEncoder.encode(icomponent,"UTF-8");
					query +="&COMPONENT="+eicomponent;
					String eifunction=URLEncoder.encode(ifunction,"UTF-8");
					query +="&FUNCTION="+eifunction;
					String eiprocess=URLEncoder.encode(iprocess,"UTF-8");
					query +="&PROCESS="+eiprocess;
					String eipubmed=URLEncoder.encode(ipubmed,"UTF-8");
					query +="&PUBMED="+eipubmed;
					
				}
				//query += "&URLS=";		   
				String urlstr="http://mimiplugin.ncibi.org/dbaccess/3.1/queryMiMIAnnot_saveUpdate_rdb.php";
			    //System.out.println("query is "+urlstr+query);
			    URL url=new URL(urlstr);
			    Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
				URLConnection conn ;
				if (CytoProxyHandler == null) 
					conn = url.openConnection();
				else conn = url.openConnection(CytoProxyHandler);
				//URLConnection conn = url.openConnection();
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				wr.write(query);
				wr.flush();					
				BufferedReader rd=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				rd.close();
				wr.close();
		}
		catch (Exception e){
   			//System.out.println(e.getMessage());
   			//System.out.println(e.getLocalizedMessage());
   			//System.out.println(e.);
        }		
	}*/
	private void doSaveUpdate(){
		try{	
			//content.setText(annotation);
			if (annotSetName.equals("MiMI")){
				annotation=content.getText();
				JOptionPane.showMessageDialog(this,"MiMI is reserved, please choose another name.");
				
				//content.setText(" ");
			}
			
		   if (content != null && !annotSetName.equals("MiMI")){				
				String lid=URLEncoder.encode(id,"UTF-8");
				String annot=URLEncoder.encode(content.getText().replaceAll("'", "").replaceAll("\n",""),"UTF-8");	
				//System.out.println("annot before save is ["+annot);
				String annotsetname=URLEncoder.encode(annotSetName.replaceAll("'", ""),"UTF-8");
				if (sharebutton.isSelected())
					share="1";
				else share ="0";
				
				//System.out.println("before save share button is  "+share);
				String urlstr="http://mimiplugin.ncibi.org/dbaccess/3.2/queryMiMIAnnot_saveUpdate_rdb.php";
				URL url=new URL(urlstr);
				Proxy CytoProxyHandler = ProxyHandler.getProxyServer();
				URLConnection conn ;
				if (CytoProxyHandler == null) 
					conn = url.openConnection();
				else conn = url.openConnection(CytoProxyHandler);
				//URLConnection conn = url.openConnection();
				conn.setUseCaches(false);
				conn.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
				String query="TABLE="+table+"&ID="+lid+"&SETNAME="+annotsetname+"&ANNOTATION="+annot+"&USERID="+userID+"&SHARE="+share;
				
				if (nodeOrEdge==1){				
					
					String name =URLEncoder.encode(nameText.getText(),"UTF-8");
					query +="&GENENAME="+name;
					String alias=URLEncoder.encode(talias.getText(),"UTF-8");
					query +="&ALIAS="+alias;
					String chromosome=URLEncoder.encode(tchromosome.getText(),"UTF-8");
					query += "&CHROMOSOME="+chromosome;					
					String maploc=URLEncoder.encode(tmaploc.getText(),"UTF-8");
					query += "&MAPLOC="+maploc;					
					String locustag=URLEncoder.encode(tlocustag.getText(),"UTF-8");
					query += "&LOCUSTAG="+locustag;	
					String taxid=URLEncoder.encode(ttaxid.getText(),"UTF-8");
					query += "&TAXID="+taxid;			
					String taxonomyName=URLEncoder.encode(ttaxonomyName.getText(),"UTF-8");
					query +="&TAXNAME="+taxonomyName;		
					String type=URLEncoder.encode(ttype.getText(),"UTF-8");
					query +="&TYPE="+type; 								
					String description = URLEncoder.encode(tdescription.getText(),"UTF-8");
					query +="&DESCRIPTION="+description;					
					String component=URLEncoder.encode(tcomponent.getText(),"UTF-8");
					query +="&COMPONENT="+component;
					String function=URLEncoder.encode(tfunction.getText(),"UTF-8");
					query +="&FUNCTION="+function;							
					String process=URLEncoder.encode(tprocess.getText(),"UTF-8");
					query += "&PROCESS="+process;
					String complex=URLEncoder.encode(tcomplex.getText(),"UTF-8");
					query +="&COMPLEX="+complex;
				}
				if (nodeOrEdge==0){
					String name =URLEncoder.encode(nameText.getText(),"UTF-8");
					query +="&INTGENENAME="+name;
					String type=URLEncoder.encode(titype.getText(),"UTF-8");
					query +="&TYPE="+type; 
					String prov=URLEncoder.encode(tiprovenance.getText(),"UTF-8");
					query +="&PROVENANCE="+prov;
					String comp=URLEncoder.encode(ticomponent.getText(),"UTF-8");
					query +="&COMPONENT="+comp;
					String func=URLEncoder.encode(tifunction.getText(),"UTF-8");
					query +="&FUNCTION="+func;
					String process=URLEncoder.encode(tiprocess.getText(),"UTF-8");
					query += "&PROCESS="+process;
					String pubmed=URLEncoder.encode(tipubmed.getText(),"UTF-8");
					query +="&PUBMED="+pubmed;					
				}				
				for (int i=0;i<urlList.getModel().getSize();i++){
					String urlString =URLEncoder.encode((String) urlList.getModel().getElementAt(i),"UTF-8");
					query += "&URLS[]="+urlString;
				}	
				if (urlList.getModel().getSize()==0)
					query +="&URLS[]="+"";
				//System.out.println("url is "+url);
				//System.out.println("save query is "+query);
				
				wr.write(query);
				wr.flush();					
				BufferedReader rd=new BufferedReader(new InputStreamReader(conn.getInputStream()));
				rd.close();
				wr.close();				
				if (!annotSetNameList.contains(annotSetName)){
					annotSetNameList.add(annotSetName);	
					annotSetList.addItem(annotSetName);
				}
			}
		}
		catch (Exception e){
   			//System.out.println(e);
        }		
	}
	
	private void getNodeAttributes(CyNode cynode){
		String id=cynode.getIdentifier();	
		//System.out.println("id is "+id);
		alias=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.otherNames")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.otherNames") : " ";
		chromosome=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.chromosome")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.chromosome") : " ";
		maploc=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.map_loc")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.map_loc") : " ";
		locustag=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.locustag")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.locustag") : " ";
		taxid=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.taxid")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.taxid") : " ";
		taxonomyName=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.organism")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.organism"):" ";			
		type=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.gene type")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.gene type"):" "; 		                       
        description=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.description")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.description"): " ";	                       
        component=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.component")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.component"):" ";
        function=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.function")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.function"):" ";
        process=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.process")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.process"):" ";
        complex=(Cytoscape.getNodeAttributes().hasAttribute(id, "Gene.complex")) ? Cytoscape.getNodeAttributes().getStringAttribute(id, "Gene.complex"): " ";
        
	}
	
	private void getEdgeAttributes(CyEdge cyedge){
		String id =cyedge.getIdentifier();			
	    itype=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.interactiontype")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.interactiontype"): " ";
	    name=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.geneName")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.geneName"): " ";
		iprovenance=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.provenance")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.provenance"): " ";		
		icomponent=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.component")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.component"): " ";				
		ifunction=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.function")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.function"): " ";
		iprocess=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.process")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.process") : " ";
		ipubmed=(Cytoscape.getEdgeAttributes().hasAttribute(id, "Interaction.pubmed")) ? Cytoscape.getEdgeAttributes().getStringAttribute(id, "Interaction.pubmed"): " ";
		
		
	}
	
	class TheMouseListener implements MouseListener{
		public TheMouseListener (){}
		public void mouseClicked(MouseEvent e) {			
			long curClick=e.getWhen();				
			if ((e.getClickCount()==2) && (MiMIPlugin.lastClick !=curClick)){
				 MiMIPlugin.lastClick=curClick;	
				 String selectedUrl = (String) urlList.getSelectedValue();
				 if (selectedUrl !=null)
						
					 BareBonesBrowserLaunch.openURL(selectedUrl);
			}		
		}
		
		public void mousePressed(MouseEvent e) {		
		}
		public void mouseEntered(MouseEvent e) {		
		}
		public void mouseExited(MouseEvent e) {		
		}
		public void mouseReleased(MouseEvent e) {			
		}
		
	}		
}

