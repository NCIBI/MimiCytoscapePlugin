<?
//ini_set("memory_limit","1024M");
include ('function.php');
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDB\PUB
$ids=(!empty($_POST['ID']))? $_POST['ID'] : $_GET['ID'];
$type=(!empty($_POST['TYPE']))? $_POST['TYPE'] : $_GET['TYPE'];
$organismID=(!empty($_POST['ORGANISMID']))? $_POST['ORGANISMID'] : $_GET['ORGANISMID'];
$moleculeType=(!empty($_POST['MOLECULETYPE']))? $_POST['MOLECULETYPE'] : $_GET['MOLECULETYPE'];
$dataSource=(!empty($_POST['DATASOURCE']))? $_POST['DATASOURCE'] : $_GET['DATASOURCE'];
$steps=(!empty($_POST['STEPS']))? $_POST['STEPS'] : $_GET['STEPS'];
$condition=(!empty($_POST['CONDITION']))? $_POST['CONDITION'] : $_GET['CONDITION'];
$filter=(!empty($_POST['FILTER']))? $_POST['FILTER'] : $_GET['FILTER'];
$rmtIP =$_SERVER['REMOTE_ADDR'];
//$rmtHost=$_SERVER['REMOTE_HOST' ];
$rmtHost=gethostbyaddr($rmtIP);
$pluginVersion='3.1';

//save user query status 
$db_mimianno="MiMIAnnotation";
//connect to database server with "usermimianno" account
conn_db_more();
mssql_select_db($db_mimianno);
$query="exec R2.UserQueryStat '$rmtIP','$rmtHost','$ids','$organismID','$moleculeType','$dataSource','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
mssql_close();

$db="mimiR2";
$ret="";
//connect to database server
conn_db();
mssql_select_db( $db);

//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
        $query="exec mimiCytoPlugin.denormMimiR2Interaction '$ids', $steps, $organismID, '$moleculeType','$dataSource',$condition,'$filter',$type";
}
//if search type is molecule id  
if($type==1){
   $query ="exec mimiCytoPlugin.denormMimiR2Interaction '$ids',1,-1,'null',-1,-1,'null',$type";
}

$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
	$ret.=$myrow[0]."~".$myrow[3].",";
}
print $ret;

mssql_close();
?>
