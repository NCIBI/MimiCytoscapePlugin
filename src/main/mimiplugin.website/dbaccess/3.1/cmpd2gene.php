<?
include ('function.php');
$inputCmpdList =$_POST['CMPDLIST'];

$db="humdb";
conn_db_db3();
mssql_select_db($db);
$query="exec denorm.cmpd2gene '$inputCmpdList'";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results)){
               $ret.="$myrow[0]/////$myrow[1]\n";
}
print $ret;
mssql_close();
?>
