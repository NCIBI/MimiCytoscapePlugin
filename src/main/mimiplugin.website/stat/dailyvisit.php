<?
include('function.php');
$year=$_GET['YEAR'];
$begin=$_GET['BEGIN'];
$end=$_GET['END'];
conn_db_more();
$db="MiMIAnnotation";
mssql_select_db($db);
$visits;

$rslt="<table><tr><td>Date</td><td>Query Number</td></tr>";
for ($month=$begin; $month<=$end; $month++){
	for ($day=1; $day<=31; $day++){
		$nextday=$day+1;
		$query="Select count(*) from r2.userstat where visittime >'$year-$month-$day' and visittime<'$year-$month-$nextday'";
       		// print "query is $query\n";
        	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");       
		if ($myrow=mssql_fetch_array($result))
			$visits=$myrow[0];
                if ($visits !=0)
			$rslt .="<tr><td>$month/$day</td><td>$visits</td></tr>";
	}
}
mssql_close();
print "$rslt</table>";

?>
