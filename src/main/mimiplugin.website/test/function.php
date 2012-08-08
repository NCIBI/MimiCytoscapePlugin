<?php
$DILEMETER="|#|";
//connect to database server
function conn_db(){
//$server="dbx.ncibi.org:1434";//pub
$server="dbx.ncibi.org:1435";//db3
$user="userMimiCytoPlugin";
//$pwd="GoBlue08!";
$pwd="GoBlue09";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}
?>
