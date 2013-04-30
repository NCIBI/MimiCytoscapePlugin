<?
include ('functionBeth.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$ids=$_GET['ID'];
$type=$_GET['TYPE'];
$organismID=$_GET['ORGANISMID'];
$moleculeType=$_GET['MOLECULETYPE'];
$dataSource=$_GET['DATASOURCE'];
$steps=$_GET['STEPS'];
$condition=$_GET['CONDITION'];
$filter=$_GET['FILTER'];
$nbrs=0;
$db="mimiR2";
$ret="";
print "$ids\n$type\n$organismID\n$moleculeType\n$dataSource\n$steps\n";
//connect to database server
$cn=conn_db();
mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
  $stmt = mssql_init("mimiCytoPlugin.mimiR2InteractionSearch_sp");
  mssql_bind($stmt, "@list", $ids, SQLVARCHAR, false);
  mssql_bind($stmt, "@steps", $steps, SQLINT4, false);
  mssql_bind($stmt, "@taxid", $organismID, SQLVARCHAR, false);
  mssql_bind($stmt, "@nbrs", $nbrs, SQLINT4, false);
  mssql_bind($stmt, "@moleculeType", $moleculeType, SQLVARCHAR, false);
  mssql_bind($stmt, "@dataSource", $dataSource, SQLVARCHAR, false);
  mssql_bind($stmt, "@condition", $condition, SQLINT4, false);
  mssql_bind($stmt, "@filter", $filter, SQLVARCHAR, false);
  mssql_bind($stmt, "@queryType",$type , SQLVARCHAR, false);

  $result = mssql_execute($stmt);
  unset($stmt);  // <---VERY important


        //$query="exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType','$dataSource',$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids',1,-1,0,'null',-1,-1,'null',$type";
}
//$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results))
	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]/////$myrow[7]/////$myrow[8]\n";
print $ret;

mssql_close($cn);

?>
