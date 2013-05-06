<?
 include ('function.php');
 $userid=$_GET['ID'];
 $confirmhash=$_GET['CONFIRM_HASH'];
 if (!$confirmhash){
	print "Sorry, activation failed!";
	exit;
 }
 //print "comfirtm is $confirmhash\n";
 //set email flag for userid 
 $db="MiMIAnnotation";
 conn_db_more();
 mssql_select_db($db);
 $query="exec R2.setEmailFlag $userid";
 $result=mssql_query($query);
 if ($result){
	print "Your account is activated succefully. You can log in now!";
 }
else {
	print "Sorry, activation failed!";
}


?>
