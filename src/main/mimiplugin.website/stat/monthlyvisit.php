<?
include('function.php');
$year=$_GET['YEAR'];
$begin=$_GET['BEGIN'];
$end=$_GET['END'];
conn_db_more();
$db="MiMIAnnotation";
mssql_select_db($db);
$visits;

$rslt="<table><tr><td>Month</td><td>Query Number</td></tr>";
for ($month=$begin; $month<=$end; $month++){
	$next=$month+1;
	$query="Select count(*) from r2.userstat where visittime >'$year-$month-1' and visittime<'$year-$next-1'";
       // print "query is $query\n";
        $result=mssql_query($query)or die("Sorry, \"$query\" failed.");       
	if ($myrow=mssql_fetch_array($result))
		$visits=$myrow[0];
	$rslt .="<tr><td>$month</td><td>$visits</td></tr>";
}
mssql_close();
print "$rslt</table>";

?>
