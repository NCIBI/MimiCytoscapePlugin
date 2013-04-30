<?
ini_set("memory_limit","1024M");
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDBX
$type=$_POST['TYPE'];
$geneIds = $_POST['GENEIDS'];
$intIds=$_POST['INTERACTIONIDS'];
$db="mimiR2v1";
$ret="";
conn_db();
mssql_select_db( $db);

//attributes
$query="exec mimiCytoPlugin.denormMimiR2Attributes $type,'$geneIds','$intIds'";
	$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
 	$len=mssql_num_fields($results);
	//print "type is $type and len is $len\n";
        while ($myrow = mssql_fetch_array($results)){
		for ($i=0;$i<$len-1;$i++){
			if ($myrow[$i])
        			$ret.="$myrow[$i]/////";
			else $ret.=" /////";
		}
		if ($myrow[$len-1])
			$ret.=$myrow[$len-1]."\n";
		else $ret.=" \n";
   	}
print $ret;
mssql_close();
?>
