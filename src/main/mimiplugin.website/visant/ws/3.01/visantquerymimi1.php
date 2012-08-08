<?
ini_set("memory_limit","1024M");
include ('/var/www/sites/mimiplugin.ncibi.org/dbaccess/3.01/function.php');
include ('functions.php');
//Written by Jing Gao
//This script is called by mimi visant plugin to query mimi database on 
//NCIBIDB\PUB

$para=(isset($_GET))? $_GET : $_POST;
/*foreach ($get as $command => $value) {
   print ("$command=>$value\n\n");
}*/


if (isset($para['command'])){
$command=trim($para['command']);
if (strcmp($command,"lookup") != 0 && strcmp($command,"unilink") != 0 && strcmp($command,"time_image_jpg") != 0)  {
	$ret=processRequest($para);
	//$targetUrl=$para['target'];
	//$ret=readRemote($targetUrl);
	print $ret;
}
else{
$arr = array("ath" =>3702, "cel" =>6239 ,"dme" =>7227 ,"eco" =>83333 ,"hsa" =>9606 ,"mmu" =>10090 ,"rno" =>10116 ,"sce" =>4932);
$ids=(!empty($_POST['src']))? $_POST['src'] : $_GET['src'];
$organism=(!empty($_POST['species']))? $_POST['species'] : $_GET['species'];  
$organismID=$arr[$organism];
//print "orgid is $organismID";
$type=0;
$moleculeType='All Molecule Types';
$dataSource='All Data Sources';
$steps='1';
$condition='4';
$filter='no';
$rmtIP =$_SERVER['REMOTE_ADDR'];
$rmtHost=gethostbyaddr($rmtIP);
$pluginVersion='3.0.1';


$ret=$ids."\t"."src_11,".$ids."\t"."original,".$ids."\t"."ORF@@";
//$ret=$ids."\t"."src_11,"."NSP116\tCommon Name,NUP116\tCommon Name,abc[function]\tDescription,".$ids."\t"."original,".$ids."\t"."ORF@@";
//save user query status
$db_mimianno="MiMIAnnotation";
//connect to database server with "usermimianno" account
conn_db_more();
mssql_select_db($db_mimianno);
$query="exec R2.UserQueryStat_VA' $rmtIP','$rmtHost','$ids','$organismID','$moleculeType','$dataSource','$pluginVersion'";
mssql_query($query)or die("Sorry, \"$query\" failed.");
mssql_close();

$db="mimiR2";
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
	$ret.="$myrow[0]*$myrow[3]:M0045:null:null,";
}

mssql_close();
print $ret;
}
}else print "Error: No Command Patameter specified";
?>
