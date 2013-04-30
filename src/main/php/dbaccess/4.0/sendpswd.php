<?php
include ('function.php');
$email=$_POST['EMAIL'];
//$to      = "fake@fakehost..com";
$db="MiMIAnnotation";
conn_db_more();
mssql_select_db($db);
$pswd="";
$ret="";
//get paswd
$query = "exec R2.getPswd  '$email'";
//print "$query\n";
$result=mssql_query($query);
if (!$result){
	print "-1"; exit;
}
if ($myrow =mssql_fetch_array($result)){
        $pswd="$myrow[0]";
}
else {print"-1"; exit;}//no email found 
$subject = "DO NOT REPLY THIS EMAIL";
$message = "Your password is:$pswd";
//print "message is $message\n";
$headers = "From: mimiplugin@portal.ncibi.org";
if ($validate=mail($email, $subject, $message, $headers))
   print "1\n";//sent email OK
else print "0\n";//bad email
?> 

