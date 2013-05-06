<?
include ('function.php');
//This script is called by mimi cytoscape plugin to query taxonomy name from Taxonomy table gene database on ncibidbx server 
$taxID=$_GET['TAXID'];

$db="gene";
$ret="";
conn_db();
mssql_select_db( $db);
$query="SELECT scientificName FROM Taxonomy WHERE taxID='$taxID'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$ret=$myrow[0]."\n";	
}
print $ret;
?>
