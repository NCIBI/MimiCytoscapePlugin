<?php
//connect to database server
function conn_db(){
//$server="dbx.ncibi.org:1434";//pub
//$server="dbx.ncibi.org:1436";//db4
$server="dbx.ncibi.org:1435";//db3
//$server="dbx.ncibi.org:1433";//dbx
//jdbc:sqlserver://ncibidb4.bicc.med.umich.edu\\sqltest;selectMethod=cursor;databaseName=pubmed


$user="userMimiCytoPlugin";
//$pwd="GoBlue08!";//pub paswd
$pwd="GoBlue08";//db3 paswd
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function conn_db_more(){
$server="dbx.ncibi.org:1434";
//$server="dbx.ncibi.org:1436";
//$server="dbx.ncibi.org:1435";
$user="userMimianno";
$pwd="userMimianno";//pub
//$pwd="GoBlue08";//db3
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function conn_db_1434(){
$server="dbx.ncibi.org:1433";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function conn_db_db3(){
$server="dbx.ncibi.org:1435";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


?>
