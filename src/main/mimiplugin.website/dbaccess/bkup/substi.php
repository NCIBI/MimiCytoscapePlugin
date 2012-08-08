<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBI-DBX
$ids=$_POST['ID'];
$type=$_POST['TYPE'];
$organismID=$_POST['ORGANISMID'];
$moleculeType=$_POST['MOLECULETYPE'];
$dataSource=$_POST['DATASOURCE'];
$steps=$_POST['STEPS'];
$nbrs=0;

//print "taxid is $organismID\n type is $moleculeType\n source is $dataSource\n";
//connect to ncibi-dbx
$server="dbx.ncibi.org";
$user="ncibiuser";
$pwd="GoBlue!";
$db="mimi042707";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
	if (strcmp($organismID,"-0") && strcmp($moleculeType,"-0") && strcmp($dataSource,"-0") ){
                $query="exec InteractionSearch1 '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource";
	}
	if (strcmp($organismID,"-0") && strcmp($moleculeType,"-0") && !strcmp($dataSource,"-0")){
                $query="exec InteractionSearch2 '$ids', $steps, $organismID,$nbrs,'$moleculeType'";
	}
	if (strcmp($organismID,"-0") && !strcmp($moleculeType,"-0") && strcmp($dataSource,"-0")){
		$query="exec InteractionSearch3 '$ids', $steps, $organismID,$nbrs,$dataSource";
	}
	if (strcmp($organismID,"-0") && !strcmp($moleculeType,"-0") && !strcmp($dataSource,"-0")){
		$query="exec InteractionSearch4 '$ids', $steps, $organismID, $nbrs";
	}
	if (!strcmp($organismID,"-0") && strcmp($moleculeType,"-0") && strcmp($dataSource,"-0")) {
		$query= "exec InteractionSearch5 '$ids', $steps, $nbrs,'$moleculeType',$dataSource";
	}
	if (!strcmp($organismID,"-0") && strcmp($moleculeType,"-0") && !strcmp($dataSource,"-0") ) {
		 $query= "exec InteractionSearch6 '$ids', $steps, $nbrs,'$moleculeType'";
	}
	if (!strcmp($organismID,"-0") && !strcmp($moleculeType,"-0") && strcmp($dataSource,"-0")) {
		$query ="exec InteractionSearch7 '$ids', $steps, $nbrs,$dataSource";
	}
	if (!strcmp($organismID,"-0") && !strcmp($moleculeType,"-0") && !strcmp($dataSource,"-0") ) {
		$query= "exec InteractionSearch8 '$ids', $steps, $nbrs";
	}

	$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
	while ($myrow = mssql_fetch_array($results))
		$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]\n";
	print $ret;
}

//if search type is molecule id list, 
if($type==1){
   $query ="exec InteractionSearch9 $ids,1,0";
   $results=mssql_query($query)or die("Sorry, \"$query\" failed.");
   while ($myrow = mssql_fetch_array($results))
	 $ret.=$myrow[0]."/////".$myrow[1]."/////".$myrow[2]."/////".$myrow[3]."/////".$myrow[4]."/////".$myrow[5]."\n";
   print $ret; 
}


//attributes
if ($type >= 10){
	if ($type==10){
		$query="exec MoleculeName_sp";
	}
	if ($type==11){
                $query="exec MoleculeDescription_sp";
        }
	if ($type==12){
                $query="exec MoleculeBiologicalProcess_sp";
        }
	if ($type==13){
                $query="exec MoleculeCellularComponent_sp";
        }
	if ($type==14){
                $query="exec MoleculeMolecularFunction_sp";
        }
	if ($type==15){
                $query="exec MoleculeOrthologue_sp";
        }
	if ($type==16){
                $query="exec MoleculeProvenance_sp";
        }

	if ($type==17){
                $query="exec MoleculePtm_sp";
        }
	if ($type==18){
                $query="exec MoleculeStruBioSite_sp";
        }
	if ($type==19){
                $query="exec MoleculeTaxAndType_sp ";
        }

	$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
 	$len=mssql_num_fields($results);
	//print "type is $type and len is $len\n";
        while ($myrow = mssql_fetch_array($results)){
		for ($i=0;$i<$len-1;$i++){
			if ($myrow[$i])
        			$ret.="$myrow[$i]/////";
			else $ret.=" /////";
		}
		if ($myrow[$len-1])
			$ret.=$myrow[$len-1]."\n";
		else $ret.=" \n";
   	}
   print $ret;

}
?>
