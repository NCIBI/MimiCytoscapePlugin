<?
include ('function.php');
//This script is called by mimi cytoscape plugin to query annotation set list of a node or edge from NodeAnnot or EdgeAnnot table on NCIBI-DBX server
$nodeOredge=$_POST['TABLE'];
$table="R2.".$nodeOredge."AnnotSet";
$ID=$_POST['ID'];
$userID =$_POST['USERID'];
$db="MiMIAnnotation";
$ret;
conn_db_more();
mssql_select_db( $db);
if (strcmp($nodeOredge,"Node")==0){
	$query="exec R2.nodesetname $ID,$userID";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	while ($myrow = mssql_fetch_array($result)){
		$ret .=$myrow[0]."\n";	
	}
	$query="exec R2.nodesetname $ID,0";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	while ($myrow = mssql_fetch_array($result)){
        	$ret .=$myrow[0]."\n";
	}
}
if (strcmp($nodeOredge,"Edge")==0){
	$query="exec  R2.edgesetname '$ID',$userID";
        $result=mssql_query($query)or die("Sorry, \"$query\" failed.");
        while ($myrow = mssql_fetch_array($result)){
                $ret .=$myrow[0]."\n";
        }
	$query="exec  R2.edgesetname '$ID',0";
        $result=mssql_query($query)or die("Sorry, \"$query\" failed.");
        while ($myrow = mssql_fetch_array($result)){
                $ret .=$myrow[0]."\n";
        }

}
print $ret;
?>
