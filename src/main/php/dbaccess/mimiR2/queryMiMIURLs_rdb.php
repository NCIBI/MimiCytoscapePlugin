<?
include('function.php');
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot table microarray?  on 
//NCIBI-DBX server
$table="R2.".$_POST['TABLE']."URLs";
$setID=$_POST['SETID'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db( $db);
$query="SELECT url FROM $table  WHERE annotSetID=$setID";
$result=mssql_query($query);//or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret .=$myrow[0]."\n";	
}
print $ret;
?>
