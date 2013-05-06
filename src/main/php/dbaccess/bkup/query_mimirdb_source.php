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
$condition=$_POST['CONDITION'];
$filter="no";
$nbrs=0;

//print "taxid is $organismID\n type is $moleculeType\n source is $dataSource\n";
//connect to ncibidbx
$server="dbx.ncibi.org:1433";
$user="ncibiuser";
$pwd="GoBlue!";
$db="mimi042707";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
        $query="exec InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter',$type";
}

//if search type is molecule id list, currently not used
if($type==1){
   $query ="exec InteractionSearch_sp $ids,1,-1,0,'null',-1,-1,'null',$type";
}

$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results))
	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]\n";
print $ret;



//attributes
if ($type >= 10){
	if ($type==10){
		$query="exec MoleculeName_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
	}
	if ($type==11){
                $query="exec MoleculeDescription_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==12){
                $query="exec MoleculeBiologicalProcess_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==13){
                $query="exec MoleculeCellularComponent_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==14){
                $query="exec MoleculeMolecularFunction_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==15){
                $query="exec MoleculeOrthologue_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==16){
                $query="exec MoleculeProvenance_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }

	if ($type==17){
                $query="exec MoleculePtm_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==18){
                $query="exec MoleculeStruBioSite_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==19){
                $query="exec MoleculeTaxAndType_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if($type==20){
                $query="exec InteractionBioSite_sp'$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }	
	if($type==21){
                $query="exec InteractionCondition_sp'$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if ($type==22){
                $query="exec InteractionDescription_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
        if ($type==23){
                $query="exec InteractionExp_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }

        if ($type==24){
                $query="exec InteractionLocation_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
        if ($type==25){
                $query="exec InteractionProvenance_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
        if ($type==26){
                $query="exec InteractionType_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
        }
	if($type==27){
		$query="exec InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter'";
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
