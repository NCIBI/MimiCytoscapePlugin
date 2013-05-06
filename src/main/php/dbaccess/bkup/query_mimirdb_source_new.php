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
$filter=$_POST['FILTER'];
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
//if search type is molecule id  
if($type==1){
   $query ="exec InteractionSearch_sp '$ids',1,-1,0,'null',-1,-1,'null',$type";
}
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results))
	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]\n";
print $ret;


?>