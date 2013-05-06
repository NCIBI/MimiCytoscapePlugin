<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$geneid=$_GET['ID'];
$organismID=$_GET['ORGANISMID'];
$moltype = $_GET['MOLTYPE'];
$datasource = $_GET['DATASOURCE'];
$db="mimiR2";
$ret="";
//connect to database server
conn_db();
mssql_select_db( $db);

$query="exec mimiCytoPlugin.denormMimiR2PrecomputeExpand $geneid, $organismID, '$moltype','$datasource'";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($results)){
        $ret=$myrow[0]."\n";
}
print $ret;
mssql_close();
?>
