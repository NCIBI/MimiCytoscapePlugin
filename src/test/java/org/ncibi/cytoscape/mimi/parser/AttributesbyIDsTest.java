package org.ncibi.cytoscape.mimi.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;


public class AttributesbyIDsTest
{
    @Test
    public void testParsingPathwayStuff() throws IOException {
        String input = "1436/////pathway/////pathway 1 [path:hsa01]\n";
        input += "1436/////pathway/////pathway 2 [path:hsa02]\n";
        input += "1537/////pathway/////pathway 3 [path:hsa03]\n";
        input += "1537/////pathway/////pathway 4 [path:hsa04]";
        
        InputStream is = new ByteArrayInputStream(input.getBytes());
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        
        //get gene pathway info
        String line = "";
        String curgeneid="";
        String description="";
        String geneid = "";
        Boolean isfirstline=true;
        while ((line = rd.readLine()) != null){
            System.out.println(line);
            String [] res=line.split("/////");
            if (res.length==3){
                geneid=res[0];
                String attrValue = res[2].trim();
                if (!isfirstline){
                    if (curgeneid.equalsIgnoreCase(geneid)) {
                        description += "; " + attrValue;
                    }
                    else if (!curgeneid.equalsIgnoreCase(geneid)){
                        System.out.println("A:" + description);
                        curgeneid=geneid; 
                        description = attrValue;
                    }
                }
                else {
                    isfirstline=false;
                    curgeneid=geneid;
                    description += attrValue;
                }             
            }               
        }
        rd.close();
        if (description.length()>2)
            System.out.println("B:" + description);
    }
}
