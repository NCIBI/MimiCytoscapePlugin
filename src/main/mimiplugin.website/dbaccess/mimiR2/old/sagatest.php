<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query gene database on 
//NCIBIDBX
$input=$_GET['PARAM'];
$db="mimiR2";
$ret="";
conn_db();
mssql_select_db( $db);
$query="exec mimiCytoPlugin.mimiR2SagaKegg '$input'";
print $query;
//$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
//while ($myrow = mssql_fetch_array($result)){
//	$ret.=$myrow[0]."\t".$myrow[1]."\n";	
//}
print $ret;
?>
