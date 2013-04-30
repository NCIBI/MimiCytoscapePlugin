<?
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot on 
//NCIBI-DBX server
$table=$_GET['TABLE'];
$setName=$_GET['SETNAME'];
$ID=$_GET['ID'];
$userID=$_GET['USERID'];

//connect to ncibidbx
$server="dbx.ncibi.org";
$user="mimianno";
$pwd="goblue07@";
$db="MiMIAnnotation";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
if (strcmp($setName, "MiMI")==0){
	$getSetID="SELECT annotSetID FROM ".$table."AnnotSet WHERE mimiID='$ID' AND annotSetName='$setName'";
}
else {
	$getSetID="SELECT TOP 1 annotSetID FROM ".$table."AnnotSet WHERE mimiID='$ID' AND annotSetName='$setName' AND userID=$userID ORDER BY datestamp DESC";
}
//print "$getSetID\n";
$result=mssql_query($getSetID)or die("Sorry, \"$getSetID\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$setID=$myrow[0];	
	$query="SELECT * FROM ".$table."Annot WHERE annotSetID=$setID";
	//print "$query\n";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($myrow = mssql_fetch_array($result)){
		if (strcmp($table,"Node")==0){
			$ret .="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]/////$myrow[7]/////$myrow[8]/////$myrow[9]/////$myrow[10]/////$myrow[11]/////$myrow[12]/////$myrow[13]\n";
		}	
		if (strcmp($table, "Edge")==0){
			$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]/////$myrow[7]/////$myrow[8]/////$myrow[9]/////$myrow[10]/////$myrow[11]/////$myrow[12]\n";
		}
	}
}
print $ret;
?>
