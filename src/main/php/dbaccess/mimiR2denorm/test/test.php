<?
include('function.php');
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot table microarray?  on 
//NCIBI-DBX server
$table="R2.".$_GET['TABLE']."URLs";
$setID=$_GET['SETID'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db( $db);
$query="exec R2.url '$table', $setID";
//print "$query\n";
$result=mssql_query($query);//or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret .=$myrow[0]."\n";	
}
print $ret;
?>
