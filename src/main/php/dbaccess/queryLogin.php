<?
//This script is called by mimi cytoscape plugin to check login info from userLogin table MiMIAnnotation database on ncibidbx server 
$username=$_GET['NAME'];
$pswd=$_GET['PWD'];

//connect to ncibidbx
$server="dbx.ncibi.org:1433";
$user="userMimianno";
$pwd="userMimianno";
$db="MiMIAnnotation";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$query="SELECT userName FROM userLogin WHERE userName='$username' AND paswd !='$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$ret="duplUserOrpaswdwrong\n";	
	print $ret;
	exit;
}

$query ="SELECT userID FROM userLogin WHERE username ='$username'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if (!mssql_fetch_array($result)){
	$query="INSERT INTO userLogin (userName, paswd) VALUES ('$username','$pswd')";	
        mssql_query($query);//or die("Sorry, \"$query\" failed.");
	$query="SELECT userID FROM userLogin WHERE userName='$username' AND paswd='$pswd'";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($myrow = mssql_fetch_array($result)){
        	$ret="$myrow[0]\n";
		print $ret;
		exit;
	}
}

$query="SELECT userID FROM userLogin WHERE userName='$username' AND paswd ='$pswd'";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
if ($myrow = mssql_fetch_array($result)){
        $ret="$myrow[0]\n";
	print $ret;
	exit;
}

?>
