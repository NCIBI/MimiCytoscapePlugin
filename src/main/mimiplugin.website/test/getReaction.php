<?
//ini_set("memory_limit","1024M");
include ('function.php');
//Written by Jing Gao
//This script is called by  Cytoscape metabolomics plugin to query humdb database 
$cmpdIDs=(!empty($_POST['CMPD']))? $_POST['CMPD'] : $_GET['CMPD'];
$pluginVersion=(!empty($_POST['PLUGINVERSION']))? $_POST['PLUGINVERSION']: $_GET['PLUGINVERSION'] ;
$rmtIP =$_SERVER['REMOTE_ADDR'];
$rmtHost=gethostbyaddr($rmtIP);

//save user status 
$db="humdb";
//connect to database server with "userMimiCytoPlugin" account
conn_db();
mssql_select_db($db);
$query="exec dbo.UserStatSP '$rmtIP','$rmtHost','$cmpdIDs','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
//get reactions
$ret="";
$query ="exec dbo.cmpdCmpdReactSP '$cmpdIDs' ";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
	for ($i=0;$i<$len-1;$i++)
               $ret.="$myrow[$i]$DILEMETER";
        $ret.=$myrow[$len-1]."\n";
}
print $ret;

mssql_close();
?>
