<?
include ('function.php');
//This script is called by mimi cytoscape plugin to check login info from userLogin table MiMIAnnotation database on ncibidbx server 
$username=$_GET['NAME'];
$pswd=$_GET['PWD'];
$db="MiMIAnnotation";
$ret="";
//conn_db_more();
conn_db();
mssql_select_db( $db);

//$query="SELECT userID FROM R2.userLogin WHERE userName='$username' AND paswd ='$pswd'";
$query="exec R2.test1";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($result)){
        $ret .="$myrow[0].\t.$myrow[1]\n";
	print $ret;
	exit;
}
mssql_close();
?>
