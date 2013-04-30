<?
$ids=$_GET['ID'];
$type=$_GET['TYPE'];
$organismID=$_GET['ORGANISMID'];
$moleculeType=$_GET['MOLECULETYPE'];
$dataSource=$_GET['DATASOURCE'];
$steps=$_GET['STEPS'];
$condition=$_GET['CONDITION'];
$filter=$_GET['FILTER'];
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
$tim1=time();
$handle=fopen($url, "rb");
$tim2=time();
$etim1 = $tim2-$tim1;
print " open url time [$etim1]\n";
while (!feof($handle)) {
	$ret .= fread($handle, 8192);
}
$tim3=time();
$etim2 =$tim3-$tim2;
print "retrieve data time [$etim2]\n";
fclose($handle);
$ret = str_replace("/////\n","\n",$ret);
$ret =preg_replace("/(^[\r\n]*|[\r\n]+)[\s\t]*[\r\n]+/", "", $ret);
print $ret;
?>
