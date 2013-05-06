<?
include ('function.php');
//$input=$_GET['PARAM'];
$db="mimiR2";
$ret="";
conn_db();
mssql_select_db( $db);

$query="exec mimiCytoPlugin.test";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
      while ($myrow = mssql_fetch_array($results))
              $ret.="$myrow[0]\n";

   print "here" ;
   print $ret;

?>
