<?php

	$user = "weymouth";
	$pw = "tew4ncibi";
	
	$server="dbx.ncibi.org:1433";//pub
	$connection = mssql_connect($server, $user,  $pw) or die("Unable to connect to server for 1433");
	$connection -> close();
	
	$server="dbx.ncibi.org:1436";//db4
	$connection = mssql_connect($server, $user,  $pw) or die("Unable to connect to server for 1436");
	$connection -> close();

	$server="dbx.ncibi.org:1435";//db3
	$connection = mssql_connect($server, $user,  $pw) or die("Unable to connect to server for 1435");
	$connection -> close();

	$server="dbx.ncibi.org:1434";//dbx
	$connection = mssql_connect($server, $user,  $pw) or die("Unable to connect to server for 1434");
	$connection -> close();


?>