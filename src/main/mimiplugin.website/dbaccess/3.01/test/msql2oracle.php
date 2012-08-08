<?

print "here\n";
//connect to database server
$server="dbx.ncibi.org:1433";
$user="jinggao";//"userMimiCytoPlugin";
$pwd="ga0j1n9";//"GoBlue08!";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");

print "here\n";
//connect to database server with "usermimianno" account
$db="microarray";
mssql_select_db($db);
$query="select  * from OPENQUERY (\"ORACLE-APP2\", 'SELECT * FROM HUMDB.GENES where rownum < 220')" ;
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
