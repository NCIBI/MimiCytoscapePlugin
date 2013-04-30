<?
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot table microarray?  on 
//NCIBIDBX server
$table=$_GET['TABLE'];
$setID=$_GET['SETID'];
//connect to ncibidbx
$server="dbx.ncibi.org";
$user="mimianno";
$pwd="goblue07@";
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
