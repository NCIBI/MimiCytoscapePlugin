<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query bionlp database on 
//NCIBIDBX
include ('function.php');
$mol1=$_GET['MOLECULE1'];
$mol2=$_GET['MOLECULE2'];
$taxid1=$_GET['TAXID1'];
$taxid2=$_GET['TAXID2'];
$db="pubmed";
$ret="test";
print "here 0";
conn_db();
print "here 1";
mssql_select_db( $db);
print "here";
$query="exec  mimiplugin.getSentencesForTaxIdAndGeneSymbolPairs '$mol1','$mol2',$taxid1,$taxid2";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\t".$myrow[1]."\t".$myrow[2]."\t".$myrow[3]."\t".$myrow[4]."\n";	
}
print $ret;
mssql_close();
?>
