<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query gene database on 
//NCIBIDBX
$input=$_POST['PARAM'];
$db="gene";
$ret="";
conn_db();
mssql_select_db( $db);
$query="exec getKoIds_sp '$input'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\t".$myrow[1]."\n";	
}
print $ret;
?>
