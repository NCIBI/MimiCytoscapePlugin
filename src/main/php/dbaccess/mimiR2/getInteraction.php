<?
include ('function.php');
$ids=$_POST['ID'];
$type=$_POST['TYPE'];
$organismID=$_POST['ORGANISMID'];
$moleculeType=$_POST['MOLECULETYPE'];
$dataSource=$_POST['DATASOURCE'];
$steps=$_POST['STEPS'];
$condition=$_POST['CONDITION'];
$filter=$_POST['FILTER'];
$rmtIP =$_SERVER['REMOTE_ADDR'];
//$rmtHost=$_SERVER['REMOTE_HOST' ];
$rmtHost=gethostbyaddr($rmtIP);
$pluginVersion='3.0';

//save user query status
$db_mimianno="MiMIAnnotation";
//connect to database server with "usermimianno" account
conn_db_more();
mssql_select_db($db_mimianno);
$query="exec R2.UserQueryStat '$rmtIP','$rmtHost','$ids','$organismID','$moleculeType','$dataSource','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
mssql_close();

$nbrs=0;
$ret="";
$handle="";
$url="";
$query="";
$urlbase="http://mimitest.ncibi.org/MimiDBTest/test2.jsp?sql=";
//if search type is gene list, get moleculeids, taxid and gene symbol
if($type==0){
	$query=urlencode("exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids', $steps, $organismID, $nbrs,'$moleculeType','$dataSource',$condition,'$filter',$type");
}
//if search type is molecule id  
if($type==1){
        $query =urlencode("exec mimiCytoPlugin.mimiR2InteractionSearch_sp '$ids',1,-1,0,'null',-1,-1,'null',$type");
}

$url =$urlbase.$query;
$handle=fopen($url, "rb");
while (!feof($handle)) {
	$ret .= fread($handle, 8192);
}
fclose($handle);
$ret = str_replace("/////\n","\n",$ret);
$ret =preg_replace("/(^[\r\n]*|[\r\n]+)[\s\t]*[\r\n]+/", "", $ret);
print $ret;
?>
