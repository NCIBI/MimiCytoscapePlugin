<?
$geneIds = $_POST['GENEIDS'];
$type=$_POST['TYPE'];
$ret="";
$handle="";
$url="";
$query="";
$urlbase="http://mimitest.ncibi.org/MimiDBTest/test2.jsp?sql=";
//attributes
$query=urlencode("exec mimiCytoPlugin.mimiR2Attributes_sp $type,'$geneIds'");
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
