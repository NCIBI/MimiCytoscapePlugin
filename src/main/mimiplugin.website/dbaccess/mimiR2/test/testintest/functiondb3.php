<?php
//connect to database server
function conn_db(){
$server="dbx.ncibi.org:1435";//"ncibidb.bicc.med.umich.edu:1434";Terry use this
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function conn_db_more(){
$server="dbx.ncibi.org:1435";
$user="userMimianno";
$pwd="userMimianno";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}
?>
