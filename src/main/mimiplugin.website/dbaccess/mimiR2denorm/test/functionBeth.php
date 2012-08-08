<?php
//connect to database server
function conn_db(){
$server="dbx.ncibi.org:1434";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
return mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function conn_db_more(){
$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
return mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}
?>
