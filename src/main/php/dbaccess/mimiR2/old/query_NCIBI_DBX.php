<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query bionlp database on 
//NCIBIDBX
$mol1=$_GET['MOLECULE1'];
$mol2=$_GET['MOLECULE2'];
//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$db="MiMIAnnotation";//STORED PROCEDURE is in database MiMIAnnotation 
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

 $query="exec getSentfromPubmed_07n_sp '$mol1','$mol2'"; //****if database name 'pubmed_07n' changed, need to change the db name in stored procedure 

$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
	$ret.=$myrow[0]."\t".$myrow[1]."\t".$myrow[2]."\t".$myrow[3]."\t".$myrow[4]."\n";	
}
print $ret;
?>
