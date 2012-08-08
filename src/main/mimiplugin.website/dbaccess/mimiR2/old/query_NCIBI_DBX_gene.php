<?
include('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query gene database on 
//NCIBIDBX
$geneName=$_GET['GENE'];
$db="gene";
$ret="";
conn_db();
mssql_select_db( $db);

$query="SELECT DISTINCT ko.koID FROM gene g, KeggGene kg, KeggOrth ko WHERE g.symbol='$geneName' AND g.geneid =kg.geneID AND kg.keggID =ko.keggID";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\n";	
}
print $ret;
?>
