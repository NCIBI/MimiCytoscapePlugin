<?
 include ('function.php');
 $userid=$_GET['ID'];
 //set email flag for userid 
 $db="MiMIAnnotation";
 conn_db_more();
 mssql_select_db($db);
 $query="exec R2.setEmailFlag $userid";
 $result=mssql_query($query);
 if ($result){
	print "Your account has been successfully activated. You can log in now!";
 }
else {
	print "Sorry, activation failed!";
}


?>
