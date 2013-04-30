<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$remoteip=$_SERVER['REMOTE_ADDR'];
print "ip is [$remoteip]\n";
$ids=$_GET['ID'];
$type=$_GET['TYPE'];
$organismID=$_GET['ORGANISMID'];
$moleculeType=$_GET['MOLECULETYPE'];
$dataSource=$_GET['DATASOURCE'];
$steps=$_GET['STEPS'];
$condition=$_GET['CONDITION'];
$filter=$_GET['FILTER'];
$nbrs=0;
$visittime=date("D d M,Y h:i:s a");
print "visit time is [$visittime]\n";
print "query is [$ids],[$type],[$organismID],[$moleculeType],[$dataSource],[$steps],[$condition]\n";

$db="mimiR2";
$ret="";
//print "$ids\n$type\n$organismID\n$moleculeType\n$dataSource\n$steps\n";
//connect to database server
conn_db();
mssql_select_db( $db);
print "enter php before sp\n";
//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
        $query="exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType','$dataSource',$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids',1,-1,0,'null',-1,-1,'null',$type";
}
$start = time();
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
echo "$query\n";
$middletime =time();
$execTime = time() - $start;
echo "query db TIME= $execTime\n";
while ($myrow = mssql_fetch_array($results))
#	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]/////$myrow[7]/////$myrow[8]\n";
	$ret.="$myrow[0]\\*$myrow[1]\\*$myrow[2]\\*$myrow[3]\\*$myrow[4]\\*$myrow[5]\\*$myrow[6]\\*$myrow[7]\\*$myrow[8]\n";
print $ret;
$exectime = time()-$middletime;
print "process result TIME=  $exectime\n";
mssql_close();
?>
