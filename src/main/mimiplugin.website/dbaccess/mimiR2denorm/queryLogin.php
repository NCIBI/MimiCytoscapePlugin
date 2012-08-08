<?
include ('function.php');
//This script is called by mimi cytoscape plugin to check login info from userLogin table MiMIAnnotation database on pub server 
$username=$_GET['NAME'];
$pswd=$_GET['PWD'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db( $db);

$query = "exec R2.getUserID  '$username','$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if (!($myrow =mssql_fetch_array($result))){
	$query="exec R2.addNewUser '$username','$pswd'";
        mssql_query($query);//or die("Sorry, \"$query\" failed.");
	$query="exec R2.getUserID  '$username','$pswd'";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($myrow = mssql_fetch_array($result)){
        	$ret="$myrow[0]\n";
		print $ret;
		exit;
	}
}
else {
	$ret="$myrow[0]\n";
                print $ret;
                exit;
}
mssql_close();
?>
