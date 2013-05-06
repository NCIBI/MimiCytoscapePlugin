<?
include ('function.php');
//This script is called by mimi cytoscape plugin to check login info from userLogin table MiMIAnnotation database on pub server 
$email=$_GET['EMAIL'];
$pswd=$_GET['PWD'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db($db);

$query = "exec R2.getUserID_301  '$email','$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
$ret="$myrow[0]\n";
print $ret;
mssql_close();
?>
