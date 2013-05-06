<?
include ('function.php');
//$input=$_GET['PARAM'];
$db="MiMIAnnotation";
$ret="";
conn_db_more();
mssql_select_db( $db);

//$query="select  annotSetID,geneID,symbol,chromosome,locustag,map_loc,alias from R2.NodeAnnot";
$query="select * from R2.NodeAnnot";
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
      while ($myrow = mssql_fetch_array($results))
	$ret .= "[$myrow[0]]\n";              
//$ret.="[$myrow[0]][$myrow[1]][$myrow[2]][$myrow[3]][$myrow[4]][$myrow[5]]\n";//[$myrow[6]][$myrow[7]][$myrow[8]][$myrow[9]]\n";

   print "here" ;
   print $ret;

?>
