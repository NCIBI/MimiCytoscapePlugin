<?
include('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBI-DBX
$ids=$_POST['ID'];
$type=$_POST['TYPE'];
$organismID=$_POST['ORGANISMID'];
$moleculeType=$_POST['MOLECULETYPE'];
$dataSource=$_POST['DATASOURCE'];
$steps=$_POST['STEPS'];
$condition=$_POST['CONDITION'];
$filter=$_POST['FILTER'];
$nbrs=0;
$rmtIP =$_SERVER['REMOTE_ADDR'];
$rmtHost=$_SERVER['REMOTE_HOST' ];
$pluginVersion='2.2';

//save user query status
$db_mimianno="MiMIAnnotation";
//connect to database server with "usermimianno" account
conn_db_more();
mssql_select_db($db_mimianno);
$query="exec R2.UserQueryStat '$rmtIP','$rmtHost','$ids','$organismID','$moleculeType','$dataSource','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
mssql_close();


//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$db="mimi";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
        $query="exec InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType',$dataSource,$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec InteractionSearch_sp '$ids',1,-1,0,'null',-1,-1,'null',$type";
}
$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
while ($myrow = mssql_fetch_array($results))
	$ret.="$myrow[0]/////$myrow[1]/////$myrow[2]/////$myrow[3]/////$myrow[4]/////$myrow[5]/////$myrow[6]\n";
print $ret;
mssql_close();

?>
