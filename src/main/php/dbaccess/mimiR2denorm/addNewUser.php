<?
include ('function.php');
//This script is called by mimi cytoscape plugin to add new user to  userLogin table MiMIAnnotation database on pub server 
$username=$_GET['NAME'];
$pswd=$_GET['PWD'];
$email=$_GET['EMAIL'];
$org=$_GET['ORG'];
$til=$_GET['TIL'];

$db="MiMIAnnotation";
$ret="";
$ret1="";
$flag=0;
conn_db_more();
mssql_select_db( $db);

$query = "exec R2.getUserID_301  '$email','$pswd',$flag";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow =mssql_fetch_array($result)){
	$ret1="$myrow[0]";
}
if (strcmp($ret1,"-1")==0){
	$query="exec R2.addNewUser_301 '$username', '$pswd','$email','$org','$til'";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($result){
		$query="exec R2.getUserID_301 '$email','$pswd',$flag";
		$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	        if ($myrow =mssql_fetch_array($result)){
			$ret="$myrow[0]\n";
		}
        }
}
else {$ret="-1\n";}
print $ret;
mssql_close();
?>
