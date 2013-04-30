<?
//ini_set("HTTP Request","POST");
//ini_set("REQUEST_METHOD","POST");
//$_SERVER['REQUEST_METHOD'] ='POST';
//This script is called by mimi cytoscape plugin to query annotation set list of a node or edge from NodeAnnot or EdgeAnnot table on NCIBI-DBX server
$table=$_GET['TABLE'];
$ID=$_GET['ID'];
$userID =$_GET['USERID'];
//print $_SERVER['REQUEST_METHOD'];
//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
$db="MiMIAnnotation";

$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$query="SELECT DISTINCT annotSetName FROM $table WHERE mimiID='$ID' AND userID=$userID ";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret .=$myrow[0]."\n";	
}
$query="SELECT annotSetName FROM $table WHERE mimiID='$ID' AND userID=0";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
        $ret .=$myrow[0]."\n";
}
print $ret;
?>
