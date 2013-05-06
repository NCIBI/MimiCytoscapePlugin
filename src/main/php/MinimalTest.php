//$server="dbx.ncibi.org:1434";//pub
//$server="dbx.ncibi.org:1436";//db4
//$server="dbx.ncibi.org:1435";//db3
//$server="dbx.ncibi.org:1433";//dbx

$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");

$rmtIP = "fake.ip.for.test"
$rmtHost = "fake.host.for.test"
$ids = "csf12r"
$organismID = 1111             
$moleculeType = 'moltype'
$dataSource = 'datasource'
$pluginVersion = '999'

$db_mimianno="MiMIAnnotation";
mssql_select_db($db_mimianno);
$query="exec R2.UserQueryStat '$rmtIP','$rmtHost','$ids','$organismID','$moleculeType','$dataSource','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
mssql_close();

"Everything worked."