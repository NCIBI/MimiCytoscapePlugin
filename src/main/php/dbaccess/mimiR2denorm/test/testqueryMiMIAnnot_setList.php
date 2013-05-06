<?
include ('function.php');
//This script is called by mimi cytoscape plugin to query annotation set list of a node or edge from NodeAnnot or EdgeAnnot table on NCIBI-DBX server
$nodeOredge=$_GET['TABLE'];
$ID=$_GET['ID'];
$userID =$_GET['USERID'];
print "here\n";

//$table="R2.$nodeOredge"."AnnotSet";
$db="MiMIAnnotation";
$ret;
print "node/edge [$nodeOredge\n";
/*conn_db_more();
mssql_select_db( $db);
if (strcmp($nodeOredge,"Node")==0){
	print "table [$table\n";
	$query="SELECT DISTINCT annotSetName FROM $table WHERE geneID=$ID AND userID=$userID ";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	while ($myrow = mssql_fetch_array($result)){
		$ret .=$myrow[0]."\n";	
	}
	$query="SELECT annotSetName FROM $table WHERE geneID=$ID AND userID=0";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	while ($myrow = mssql_fetch_array($result)){
        	$ret .=$myrow[0]."\n";
	}
}
print $ret;*/
?>
