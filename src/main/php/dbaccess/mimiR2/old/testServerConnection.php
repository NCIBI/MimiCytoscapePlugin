<?

//print "taxid is $organismID\n type is $moleculeType\n source is $dataSource\n";
//connect to ncibidbx
//$server="192.168.2.61";//ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimiCytoPlugin";
$pwd="GoBlue08!";
$db="mimiR2";
$ret="aaabbb";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);

        //$query="select *  from interaction";
  //      $query="exec test1_sp";
//	$results=mssql_query($query)or die("Sorry, \"$query\" failed.");
//	while ($myrow = mssql_fetch_array($results))
//		$ret.="$myrow[0]\n";
	print $ret;
?>
