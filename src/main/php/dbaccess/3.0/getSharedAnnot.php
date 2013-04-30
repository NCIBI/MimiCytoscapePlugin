<?
include('function.php');
$nodege = $_POST['TABLE'];
$id=$_POST['ID'];
$db="MiMIAnnotation";
$ret="";
$query;
conn_db_more();
mssql_select_db($db);
if (strcmp($nodege, "Node")==0){
	$query="exec R2.sharednodeannot $id";
}
if (strcmp($nodege, "Edge")==0){
	$query="exec R2.sharededgeannot '$id'";
}
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($result);
while ($myrow = mssql_fetch_array($result)){
	for ($i=0;$i<$len-1;$i++){
        	if ($myrow[$i]){
			$value=str_replace("\n"," ",$myrow[$i]);
                	$ret.="$value/////";
		}
                else $ret.=" /////";
        }
        if ($myrow[$len-1])
        	$ret.=$myrow[$len-1]."\n";
        else $ret.=" \n";
}

print $ret;
mssql_close();
?>
