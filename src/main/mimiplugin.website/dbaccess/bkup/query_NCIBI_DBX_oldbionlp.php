<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query bionlp database on 
//NCIBIDBX
$mol1=$_GET['MOLECULE1'];
$mol2=$_GET['MOLECULE2'];
//connect to ncibidbx
$server="dbx.ncibi.org";
$user="ncibiuser";
$pwd="GoBlue!";
$db="bionlp";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$query="select distinct d.pmid, s.secID, g1.symbol, g2.symbol, s.sentence from gene..Gene g1 join SentenceGene sg1 on (g1.geneID = sg1.geneID) join Sentence s on (sg1.sID = s.sID) join Document d on (d.dID = s.dID) join SentenceGene sg2 on (sg2.sID = s.sID) join gene..Gene g2 on (sg2.geneID = g2.geneID) where g1.symbol='".$mol1."' and g2.symbol = '".$mol2."'";

$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\t".$myrow[1]."\t".$myrow[2]."\t".$myrow[3]."\t".$myrow[4]."\n";	
}
print $ret;
?>
