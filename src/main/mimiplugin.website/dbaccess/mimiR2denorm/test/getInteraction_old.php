<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$ids=$_POST['ID'];
$type=$_POST['TYPE'];
$organismID=$_POST['ORGANISMID'];
$moleculeType=$_POST['MOLECULETYPE'];
$dataSource=$_POST['DATASOURCE'];
$steps=$_POST['STEPS'];
$condition=$_POST['CONDITION'];
$filter=$_POST['FILTER'];
$db="mimiR2";
$ret="";
//connect to database server
conn_db();
mssql_select_db( $db);
//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
        $query="exec mimiCytoPlugin.denormMimiR2Interaction '$ids', $steps, $organismID, '$moleculeType','$dataSource',$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec mimiCytoPlugin.denormMimiR2Interaction '$ids',1,-1,'null',-1,-1,'null',$type";
}
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results))
	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]\n";
print $ret;
mssql_close();
#http://mimiplugin.ncibi.org/dbaccess/mimiR2/test/testgetInteraction.php?ID=fyn&TYPE=0&ORGANISMID=9606&MOLECULETYPE=protein&DATASOURCE=All%20Data%20Sources&STEPS=1&CONDITION=2&FILTER=no
?>
