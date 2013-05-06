<?
include ('function.php');
//Written by Jing Gao
//check node/edge user annotation attribute  
$type = $_POST['TYPE'];
$ids = $_POST['IDS'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db($db);

//attributes
$query="exec R2.checkUserAnnotAttribute $type,'$ids'";
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
