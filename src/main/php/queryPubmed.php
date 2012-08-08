<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query bionlp database on 
//NCIBIDBX
include ('function.php');
$mol1=$_POST['MOLECULE1'];
$mol2=$_POST['MOLECULE2'];
$taxid1=$_POST['TAXID1'];
$taxid2=$_POST['TAXID2'];
//$db="pubmed";
//$ret="";
//conn_db();
//mssql_select_db( $db);
//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$db="pubmed";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

$query="exec  mimiplugin.getSentencesForTaxIdAndGeneSymbolPairs '$mol1','$mol2',$taxid1,$taxid2";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\t".$myrow[1]."\t".$myrow[2]."\t".$myrow[3]."\t".$myrow[4]."\n";	
}
print $ret;
mssql_close();
?>
