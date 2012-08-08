<?
//This script is called by mimi cytoscape plugin to query protein subcellular localization info from protLocation table gene database on 
//NCIBIDBX server
$geneName=$_GET['NAME'];
$taxID=$_GET['TAXID'];
$ret="";
if ($taxID==" "){
	print $ret;
	exit;
}

//connect to ncibidbx
$server="dbx.ncibi.org";
$user="ncibiuser";
$pwd="GoBlue!";
$db="gene";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$arraySize=count($geneName);
if ($arraySize==1){
			

	$query="SELECT DISTINCT subLocalization FROM protLocation WHERE uniprotID='".$geneName[0]."'"."OR (taxID='".$taxID."' AND( nameType ='name'OR nameType='Synonyms')AND name='".$geneName[0]."')";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	if ($myrow = mssql_fetch_array($result)){
		$ret=$myrow[0]."\n";	
	}
}
else if ($arraySize>1){
	$nameList="";
	for($i=0;$i<$arraySize-1;$i++){
		$nameList .="'".$geneName[$i]."',";
	}
	$nameList="(". $nameList."'".$geneName[$arraySize-1]."')";
        //print "name list is " .$nameList;

        $query="SELECT DISTINCT uniprotID,subLocalization FROM protLocation WHERE uniprotID IN ".$nameList;
        $result=mssql_query($query)or die("Sorry, \"$query\" failed.");
        while ($myrow = mssql_fetch_array($result)){
                $ret .=$myrow[0]."///".$myrow[1]."\n";
        }

	$query ="SELECT name, subLocalization FROM protLocation WHERE taxID='".$taxID."' AND (nameType ='name' OR nameType='Synonyms')AND name IN ".$nameList;	
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
        while ($myrow = mssql_fetch_array($result)){
                $ret .=$myrow[0]."///".$myrow[1]."\n";
        }
}
print $ret;
?>
