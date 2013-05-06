<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDBX
$moleculeIds=$_POST['MOLECULEIDS'];
$interactionIds=$_POST['INTERACTIONIDS'];
$type=$_POST['TYPE'];
$db="mimi";
$ret="";
conn_db();
mssql_select_db( $db);


//attributes


	$query="exec Attributes_sp '$moleculeIds','$interactionIds',$type";
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

?>
