<?
function conn_db_visant(){
//$server="dbx.ncibi.org:1433"; //dbx
$server="dbx.ncibi.org:1434"; //pub
//$server="dbx.ncibi.org:1435"; //db3
//$server="dbx.ncibi.org:1436"; //db4

$user="userVisant";
$pwd="userV1sant$";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
}


function endwith ($str, $test){
	$endsWith = substr( $str, -strlen( $test ) ) == $test;
	return $endsWith;
}


function formatInputgenes($inputgenes){
	$ret='';
	$array=split('[ ,;]',$inputgenes);
	if (count($array)>0){
		foreach($array as $value)
        		if ($value !='') $ret .= ",'" . $value . "'";
		$ret = substr($ret, 1);
        }
	return $ret;	
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
