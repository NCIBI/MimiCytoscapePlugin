<?
include("function.php");
$db="gds";
conn_db_db3();
mssql_select_db($db);
$query="exec denorm.browseGds";
$result=mssql_query($query) or die("$query failed\n");
$ret="";
$len=mssql_num_fields($result);
while ($myrow=mssql_fetch_array($result)){
	 for ($i=0;$i<$len-1;$i++){
                        if ($myrow[$i]){
                                $ret.="$myrow[$i]/////";
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
