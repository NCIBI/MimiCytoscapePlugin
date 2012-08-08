<?
function endwith ($str, $test){
	$endsWith = substr( $str, -strlen( $test ) ) == $test;
	return $endsWith;
}


function processRequest ($para){
	$i=0;
	$baseURL="http://visant.bu.edu:8080/vserver/DAI?";
	$url=$baseURL;
	foreach ($para as $command => $value) {
		if ($i !=0)
			$url .="&$command=$value"; 
		else{
			$i++;
			$url .="$command=$value";
		}
        }
        $content = '';
        if ($fp = fopen($url, 'r')) {
        // keep reading until there's nothing left
        while ($line = fread($fp, 1024)) {
                $content .= $line;
                }
        fclose($fp);
        }
        return $content;
}




function readRemote ($targeturl){
   	$content = '';
	if ($fp = fopen($targeturl, 'r')) {
   	// keep reading until there's nothing left
   	while ($line = fread($fp, 1024)) {
      		$content .= $line;
   		}
	fclose($fp);
	}
	return $content;
}
?>
