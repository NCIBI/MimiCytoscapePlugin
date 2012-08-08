<?
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$remoteip=$_SERVER['REMOTE_ADDR'];
$remoteName=$_SERVER['REMOTE_HOST' ];
$rmthttphst=$_SERVER['HTTP_HOST' ];
print "server name is [$remoteName]\n ";
print "ip is [$remoteip]\n";
print "http host is [$rmthttphst]\n";
//$ids=$_GET['ID'];
//$type=$_GET['TYPE'];
//$organismID=$_GET['ORGANISMID'];
//$moleculeType=$_GET['MOLECULETYPE'];
//$dataSource=$_GET['DATASOURCE'];
//$steps=$_GET['STEPS'];
//$condition=$_GET['CONDITION'];
//$filter=$_GET['FILTER'];
$nbrs=0;
$visittime=date("D d M,Y h:i:s a");
print "visit time is [$visittime]\n";
//print "query is [$ids],[$type],[$organismID],[$moleculeType],[$dataSource],[$steps],[$condition]\n";
/$UserQueryStat
?>
