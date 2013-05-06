<?php
include ('function.php');
$to=$_GET['EMAIL'];
//$to      = "fake@fakehost..com";
$pwd=$_GET['PWD'];
$db="MiMIAnnotation";
conn_db_more();
mssql_select_db($db);
$id="";
$ret="";
$flag=0;
//get userID for the email and paswd
$query = "exec R2.getUserID_301  '$to','$pwd', $flag";
//print "$query\n";
$result=mssql_query($query);
if (!$result){
	print "-1"; exit;
}
if ($myrow =mssql_fetch_array($result)){
        $id="$myrow[0]";
}
$subject = "DO NOT REPLY THIS EMAIL";
$message = "http://mimiplugin.ncibi.org/dbaccess/mimiR2denorm/flagEmail.php?ID=$id";
//print "$message\n";
$headers = "From: mimiplugin@portal.ncibi.org";
if ($validate=mail($to, $subject, $message, $headers))
   print "1\n";
else print "0\n";
?> 

