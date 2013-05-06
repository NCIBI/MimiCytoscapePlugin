<?
include ('function.php');
//This script is called by mimi cytoscape plugin to check login info from userLogin table MiMIAnnotation database on ncibidbx server 
$username=$_GET['NAME'];
$pswd=$_GET['PWD'];
$db="MiMIAnnotation";
$table="R2.userLogin";
$ret="";
conn_db_more();
mssql_select_db( $db);
$query="SELECT userName FROM $table WHERE userName='$username' AND paswd !='$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$ret="duplUserOrpaswdwrong\n";	
	print $ret;
	exit;
}

$query ="SELECT userID FROM $table WHERE username ='$username'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if (!mssql_fetch_array($result)){
	$query="INSERT INTO $table (userName, paswd) VALUES ('$username','$pswd')";	
        mssql_query($query);//or die("Sorry, \"$query\" failed.");
	$query="SELECT userID FROM $table WHERE userName='$username' AND paswd='$pswd'";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($myrow = mssql_fetch_array($result)){
        	$ret="$myrow[0]\n";
		print $ret;
		exit;
	}
}

$query="SELECT userID FROM $table WHERE userName='$username' AND paswd ='$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($result)){
        $ret="$myrow[0]\n";
	print $ret;
	exit;
}
mssql_close();
?>
