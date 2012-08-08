<?

//$date=$_GET['DATE'];
//connect to database server
$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
$db="MiMIAnnotation";
$column;
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db($db);
$query="select distinct ip, host from R2.UserStat ";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
print "<table border=1><tr><td>IP</td><td>HOST</td></tr><tr>";
while ($myrow = mssql_fetch_array($results)){
	for ($i=0;$i<$len-1;$i++){
		$column=$myrow[$i];
		print "<td>$column</td>";
        }
               //$ret.="$myrow[$i]\t";
        $column=$myrow[$len-1];
	print "<td>$column</td></tr><tr>";
}
print "</table>";
mssql_close();
?>
