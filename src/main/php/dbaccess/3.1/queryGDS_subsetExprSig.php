<?
include("function.php");
$gdsdataset=$_POST['GDSDATASET'];
$genelist=$_POST['GENEIDLIST'];
//$gdsdataset="gds1283";
//$genelist="1435 1436 867";
$db="gds";
//conn_db_db3();
conn_db();
mssql_select_db($db);
$query="exec denorm.getExprSigVals '$gdsdataset', '$genelist'";
//print "$query";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
$ret="";
$len=mssql_num_fields($result);
while ($myrow=mssql_fetch_array($result)){
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
