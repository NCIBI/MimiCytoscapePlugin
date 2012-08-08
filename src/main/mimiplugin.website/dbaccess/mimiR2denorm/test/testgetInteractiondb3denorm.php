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
        $query="exec mimiCytoPlugin.denormMimiR2Interaction '$ids', $steps, $organismID, '$moleculeType','$dataSource',$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec mimiCytoPlugin.denormMimiR2Interaction '$ids',1,-1,'null',-1,-1,'null',$type";
}
$start = time();
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
echo "$query\n";
$middletime =time();
$execTime = time() - $start;
echo "query db TIME= $execTime\n";
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
	 for ($i=0;$i<$len-1;$i++)
              $ret.="$myrow[$i]/////";
        $ret.=$myrow[$len-1]."\n";
}
print $ret;
$exectime = time()-$middletime;
print "process result TIME=  $exectime\n";
mssql_close();
?>
