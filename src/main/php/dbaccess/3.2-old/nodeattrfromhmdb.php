<?
include ('function.php');
$geneList =$_POST['GENELIST'];

$db="humdb";
conn_db_db3();
mssql_select_db($db);
$query="exec denorm.geneAttr '$geneList'";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
        for ($i=0;$i<$len-1;$i++)
               $ret.="$myrow[$i]/////";
        $ret.=$myrow[$len-1]."\n";
}
print $ret;
mssql_close();
?>
