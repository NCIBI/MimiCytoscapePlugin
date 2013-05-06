<?
$ret="";
$db="mimiR2";
$server="dbx.ncibi.org:1436";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");

mssql_select_db($db);
$query="select top 100 * from interaction";
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
