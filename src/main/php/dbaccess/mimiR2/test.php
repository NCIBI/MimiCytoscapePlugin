<?
	$url="http://mimitest.ncibi.org/MimiDBTest/test2.jsp?sql=exec%20mimiCytoPlugin.mimiR2InteractionSearch_sp%20'csf1r',1,9606,0,'protein','all',2,'no',0";
	$handle=fopen($url, "rb");
	//$handle=fopen("http://mimitest.ncibi.org/MimiDBTest/test2.jsp?sql=exec%20mimiCytoPlugin.mimiR2InteractionSearch_sp%20'tcf7l2',1,9606,0,'protein','all',2,'no',0","rb");
 	$handle =fopen($url, "rb");
	$contents = '';
	while (!feof($handle)) {
		$contents .= fread($handle, 8192);
	}
	fclose($handle);
print  $contents;
?>
