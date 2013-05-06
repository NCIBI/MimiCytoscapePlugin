<?
//This script is called by mimi cytoscape plugin to save annotaion to NodeAnnot or EdgeAnnot table on MiMIAnnotation NCIBIDBX server
$table=$_GET['TABLE'];
$ID=$_GET['ID'];
$setName =$_GET['SETNAME'];
$userid=$_GET['USERID'];
$annot=$_GET['ANNOTATION'];
$urls=$_GET['URLS'];
$mimiName=$_GET['NAME'];
$type=$_GET['TYPE'];
$desc=$_GET['DESC'];
$exterRef=$_GET['EXTREF'];

//connect to ncibidbx
$server="dbx.ncibi.org";
$user="mimianno";
$pwd="goblue07@";
$db="MiMIAnnotation";
$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);
$tableSet=$table."AnnotSet";
$tableAnnot=$table."Annot";
$tableUrl=$table."URLs";

if (strcmp($table,"Node")==0){
	$alias=$_GET['ALIAS'];
	$taxo=$_GET['TAXO'];
	$cellCom=$_GET['CELCOM'];
	$mulFun=$_GET['MOLFUN'];
	$biopro=$_GET['BIOPRO'];
	$ptm=$_GET['PTM'];
	$domain=$_GET['DOMAIN'];
	$ortho=$_GET['ORTH'];
        $addSet="INSERT INTO $tableSet (userID,annotSetName,datestamp,mimiID) VALUES ('$userid','$setName',GETDATE(),'$ID')";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
	$getsetID="SELECT TOP 1 annotSetID FROM $tableSet WHERE userID=$userid AND annotSetName='$setName' AND mimiID='$ID' ORDER BY datestamp DESC ";	
	$dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
	if ($myrow = mssql_fetch_array($dogetsetID)){
		$setID=$myrow[0];
		$insert="INSERT INTO $tableAnnot (mimiName,annotation,alias,type,taxonomyName,description,externalReference,cellularComponent,molecularFunction, biologicalProcess,ptm,domain,orthology,annotSetID) VALUES('$mimiName','$annot','$alias','$type','$taxo','$desc','$exterRef','$cellCom','$mulFun','$biopro','$ptm','$domain','$ortho', $setID)";
        $doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
        //print "$insert\n";

	}
}


if (strcmp($table,"Edge")==0){
	$intraid=$_GET['INTRID'];		
	$exp=$_GET['EXP'];              
	$exprov=$_GET['EXPROV'];
	$cond=$_GET['COND'];
	$explik=$_GET['EXPLIK'];
	$intsit=$_GET['INTSIT'];
	$pub=$_GET['PUB'];
	//$location=$_GET['LOCATION'];
	$addSet="INSERT INTO $tableSet (userID,annotSetName,datestamp,mimiID) VALUES ('$userid','$setName',GETDATE(),'$ID')";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
        $getsetID="SELECT TOP 1 annotSetID FROM $tableSet WHERE userID=$userid AND annotSetName='$setName' AND mimiID='$ID' ORDER BY datestamp DESC ";
        $dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
        if ($myrow = mssql_fetch_array($dogetsetID)){
                $setID=$myrow[0];
		
		$insert="INSERT INTO $tableAnnot (mimiName,annotation,type,description,experiment,condition,interactionMiMIID,experimentProvenance,experimentLikelyhood,interactionsites,publications,externalReference, annotSetID) VALUES('$mimiName','$annot','$type','$desc','$exp','$cond','$intraid','$exprov','$explik','$intsit','$pub','$exterRef',$setID)";
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
