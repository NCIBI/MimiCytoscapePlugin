<?
include ('function.php');
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot on 
//NCIBIDBX server
$nodeOrEdge=$_POST['TABLE'];
$tableAnoSet="R2.".$nodeOrEdge."AnnotSet";
$tableAnot="R2.".$nodeOrEdge."Annot";
$setName=$_POST['SETNAME'];
$ID=$_POST['ID'];
$userID=$_POST['USERID'];
$db="MiMIAnnotation";
$ret="";

conn_db_more();
mssql_select_db( $db);
if (strcmp($nodeOrEdge, "Node")==0){
	if (strcmp($setName, "MiMI")==0){
		  $getSetID="exec R2.nodeset $ID,'$setName',0";
	}
	else {
		$getSetID="exec R2.nodeset $ID,'$setName',$userID";
	}
}
if (strcmp($nodeOrEdge, "Edge")==0){
       if (strcmp($setName, "MiMI")==0){
		$getSetID="exec R2.edgeset '$ID','$setName',0";
        }
        else {
		$getSetID="exec R2.edgeset '$ID','$setName',$userID";
        }

}
//print "$getSetID\n";
$result=mssql_query($getSetID)or die("Sorry, \"$getSetID\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$setID=$myrow[0];	
	$query="exec R2.annot '$nodeOrEdge',$setID";
	//print "$query\n";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	$len=mssql_num_fields($result);
	if ($myrow = mssql_fetch_array($result)){
		for ($i=0;$i<$len-1;$i++){
                    $ret.="$myrow[$i]/////";
                }
                $ret.=$myrow[$len-1]."\n";
	}
}
print $ret;
mssql_close();
?>
