<?
include ('function.php');
//This script is called by mimi cytoscape plugin to save annotaion to NodeAnnot or EdgeAnnot table on MiMIAnnotation NCIBI-DBX server
$table=$_GET['TABLE'];
$ID=$_GET['ID'];
$setName =$_GET['SETNAME'];
$userid=$_GET['USERID'];
$annot=$_GET['ANNOTATION'];
$urls=$_GET['URLS'];

$db="MiMIAnnotation";
conn_db_more();
mssql_select_db( $db);
$tableSet="R2.".$table."AnnotSet";
$tableAnnot="R2.".$table."Annot";
$tableUrl="R2.".$table."URLs";

if (strcmp($table,"Node")==0){
	$geneName =$_GET['GENENAME'];
	$alias=$_GET['ALIAS'];
	$chromosome=$_GET['CHROMOSOME'];
        $maploc =$_GET['MAPLOC'];
        $locustag=$_GET['LOCUSTAG'];
	$taxid=$_GET['TAXID'];
	$taxonomyName=$_GET['TAXNAME'];
	$type = $_GET['TYPE'];
	$description=$_GET['DESCRIPTION'];
	$comp=$_GET['COMPONENT'];
	$fun=$_GET['FUNCTION'];
	$proc=$_GET['PROCESS'];
	$kegg=$_GET['KEGG'];
	$pathway=$_GET['PATHWAY'];
	$complex=$_GET['COMPLEX'];
        $addSet="INSERT INTO $tableSet (userID,annotSetName,datestamp,geneID) VALUES ($userid,'$setName',GETDATE(),$ID)";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
	$getsetID="SELECT TOP 1 annotSetID FROM $tableSet WHERE userID=$userid AND annotSetName='$setName' AND geneID=$ID ORDER BY datestamp DESC ";	
	$dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
	if ($myrow = mssql_fetch_array($dogetsetID)){
		$setID=$myrow[0];
		$insert="INSERT INTO $tableAnnot (annotSetID,geneID,symbol,chromosome,locustag,map_loc,alias,[type],taxonomyName,taxonomyID,description,component,[function],process,kegg,pathway,complex,annotation) VALUES($setID,$ID,'$geneName',$chromosome,'$locustag','$maploc','$alias','$type','$taxonomyName',$taxid,'$description','$comp','$fun','$proc','$kegg','$pathway','$complex','$annot')";
        $doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
        print "$insert\n";

	}
}


if (strcmp($table,"Edge")==0){
	//$intraid=$_POST['INTRID'];		
	$exp=$_POST['EXP'];              
	//$exprov=$_POST['EXPROV'];
	$cond=$_POST['COND'];
	//$explik=$_POST['EXPLIK'];
	//$intsit=$_POST['INTSIT'];
	//$pub=$_POST['PUB'];
	$location=$_POST['LOCATION'];
	$addSet="INSERT INTO $tableSet (userID,annotSetName,datestamp,mimiID) VALUES ('$userid','$setName',GETDATE(),'$ID')";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
        $getsetID="SELECT TOP 1 annotSetID FROM $tableSet WHERE userID=$userid AND annotSetName='$setName' AND mimiID='$ID' ORDER BY datestamp DESC ";
        $dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
        if ($myrow = mssql_fetch_array($dogetsetID)){
                $setID=$myrow[0];
		
		$insert="INSERT INTO $tableAnnot (mimiName,annotation,type,description,experiment,condition,provenance,bioSite,location, annotSetID) VALUES('$mimiName','$annot','$type','$desc','$exp','$cond','$prove','$biosite','$location',$setID)";
        	$doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
       }	
}
//update nodeurls or edgeurls tables 
//first delete old and then insert new 
for ($i=0;$i<count($urls);$i++){
	//print "url is ".$curUrl."\n";
	$curUrl=$urls[$i];
	$insertUrls ="INSERT INTO $tableUrl (annotSetID,url) VALUES ($setID,'$curUrl')";
	mssql_query($insertUrls);// or die("Sorry,\"$insertUrls\" failed.");
}
mssql_close() 
?>
