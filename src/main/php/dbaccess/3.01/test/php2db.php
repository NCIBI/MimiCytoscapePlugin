<?
$server="dbx.ncibi.org:1434";
$user="yourusername";
$pwd="yourpaswd";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
$db="yourdatabase";
mssql_select_db($db);
$table="yourtable";
$ret="";
$query="select top 100 * from $table ";
mssql_query($query)or die("Sorry, \"$query\" failed.");
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
