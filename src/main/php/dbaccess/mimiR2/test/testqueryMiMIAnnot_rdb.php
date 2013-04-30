<?
include ('function.php');
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot on 
//NCIBIDBX server
$nodeOrEdge=$_GET['TABLE'];
$tableAnoSet="R2.".$nodeOrEdge."AnnotSet";
$tableAnot="R2.".$nodeOrEdge."Annot";
$setName=$_GET['SETNAME'];
$ID=$_GET['ID'];
$userID=$_GET['USERID'];
$db="MiMIAnnotation";
$ret="";

conn_db_more();
mssql_select_db( $db);
if (strcmp($nodeOrEdge, "Node")==0){
	if (strcmp($setName, "MiMI")==0){
		$getSetID="SELECT annotSetID FROM $tableAnoSet WHERE geneID=$ID AND annotSetName='$setName'";
	}
	else {
		$getSetID="SELECT TOP 1 annotSetID FROM $tableAnoSet WHERE geneID=$ID AND annotSetName='$setName' AND userID=$userID ORDER BY datestamp DESC";
	}
}
if (strcmp($nodeOrEdge, "Edge")==0){
	print "place holder here\n";
}
//print "$getSetID\n";
$result1=mssql_query($getSetID)or die("Sorry, \"$getSetID\" failed.");
if ($myrow = mssql_fetch_array($result1)){
	$setID=$myrow[0];	
	//$query="SELECT *  FROM $tableAnot WHERE annotSetID=$setID";
	$query="SELECT *  FROM R2.NodeURLs";
	//print "$query\n";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	$len=mssql_num_fields($result);
	if ($myrow = mssql_fetch_array($result)){
		for ($i=0;$i<$len-1;$i++){
                        if ($myrow[$i])
                                $ret.="$myrow[$i]/////";
                        else $ret.=" /////";
                }
                if ($myrow[$len-1])
                        $ret.=$myrow[$len-1]."\n";
                else $ret.=" \n";
	}
}
print $ret;
?>
