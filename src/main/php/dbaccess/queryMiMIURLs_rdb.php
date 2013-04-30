<?
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot table microarray?  on 
//NCIBI-DBX server
$table=$_POST['TABLE'];
$setID=$_POST['SETID'];
//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
$db="MiMIAnnotation";

$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$query="SELECT url FROM ".$table."URLs  WHERE annotSetID=$setID";
$result=mssql_query($query);//or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret .=$myrow[0]."\n";	
}
print $ret;
?>
