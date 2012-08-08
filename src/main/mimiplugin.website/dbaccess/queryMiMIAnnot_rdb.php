<?
//This script is called by mimi cytoscape plugin to query annotation of a node or edge from NodeAnnot or EdgeAnnot on 
//NCIBIDBX server
$table=$_POST['TABLE'];
$setName=$_POST['SETNAME'];
$ID=$_POST['ID'];
$userID=$_POST['USERID'];

//connect to ncibidbx
$server="dbx.ncibi.org:1434";
$user="userMimianno";
$pwd="userMimianno";
$db="MiMIAnnotation";
$ret="";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
if (strcmp($setName, "MiMI")==0){
	$getSetID="SELECT annotSetID FROM ".$table."AnnotSet WHERE mimiID='$ID' AND annotSetName='$setName'";
}
else {
	$getSetID="SELECT TOP 1 annotSetID FROM ".$table."AnnotSet WHERE mimiID='$ID' AND annotSetName='$setName' AND userID=$userID ORDER BY datestamp DESC";
}

//print "$getSetID\n";
$result=mssql_query($getSetID)or die("Sorry, \"$getSetID\" failed.");
if ($myrow = mssql_fetch_array($result)){
	$setID=$myrow[0];	
	$query="SELECT * FROM ".$table."Annot WHERE annotSetID=$setID";
	//print "$query\n";
	$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
	$len=mssql_num_fields($result);
	if ($myrow = mssql_fetch_array($result)){
		for ($i=0;$i<$len-1;$i++){
                        if ($myrow[$i])
                                $ret.="$myrow[$i]/////";
                        else $ret.=" /////";
                }
                if ($myrow[$len-1])
                        $ret.=$myrow[$len-1]."\n";
                else $ret.=" \n";
	}
}
print $ret;
?>
