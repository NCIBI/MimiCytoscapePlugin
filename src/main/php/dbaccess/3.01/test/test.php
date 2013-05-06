<?
//ini_set("memory_limit","1024M");
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB

//save user query status 
//$db_mimianno="MiMIAnnotation";
//connect to database server with "usermimianno" account
//conn_db_more();
/*mssql_select_db($db_mimianno);
$query="select top 100 * from r2.userstat";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
        for ($i=0;$i<$len-1;$i++)
               $ret.="$myrow[$i]/////";
        $ret.=$myrow[$len-1]."\n";
}

mssql_close();
print "here $ret\n";*/
$ret="";
$db="mimiR2";
$ret="";
//connect to database server
conn_db();
/*mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
$query="select top 100 * from denorm.genegeneinteraction";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
	for ($i=0;$i<$len-1;$i++)
               $ret.="$myrow[$i]/////";
        $ret.=$myrow[$len-1]."\n";
}
print $ret;

mssql_close();*/
?>
