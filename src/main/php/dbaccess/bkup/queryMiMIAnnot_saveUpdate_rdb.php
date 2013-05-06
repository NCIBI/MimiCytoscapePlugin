<?
//This script is called by mimi cytoscape plugin to save annotaion to NodeAnnot or EdgeAnnot table on MiMIAnnotation NCIBI-DBX server
$table=$_POST['TABLE'];
$ID=$_POST['ID'];
$setName =$_POST['SETNAME'];
$userid=$_POST['USERID'];
$annot=$_POST['ANNOTATION'];
$urls=$_POST['URLS'];
$mimiName=$_POST['NAME'];
$type=$_POST['TYPE'];
$desc=$_POST['DESC'];
//$exterRef=$_POST['EXTREF'];
$prove=$_POST['PROVE'];
$biosite=$_POST['BIOSITE'];

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
	$alias=$_POST['ALIAS'];
	$taxo=$_POST['TAXO'];
	$cellCom=$_POST['CELCOM'];
	$mulFun=$_POST['MOLFUN'];
	$biopro=$_POST['BIOPRO'];
	$ptm=$_POST['PTM'];
	//$domain=$_POST['DOMAIN'];
	$ortho=$_POST['ORTH'];
        $addSet="INSERT INTO $tableSet (userID,annotSetName,datestamp,mimiID) VALUES ('$userid','$setName',GETDATE(),'$ID')";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
	$getsetID="SELECT TOP 1 annotSetID FROM $tableSet WHERE userID=$userid AND annotSetName='$setName' AND mimiID='$ID' ORDER BY datestamp DESC ";	
	$dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
	if ($myrow = mssql_fetch_array($dogetsetID)){
		$setID=$myrow[0];
		$insert="INSERT INTO $tableAnnot (mimiName,annotation,alias,type,taxonomyName,description,provenance,cellularComponent,molecularFunction, biologicalProcess,ptm,bioSite,orthology,annotSetID) VALUES('$mimiName','$annot','$alias','$type','$taxo','$desc','$prove','$cellCom','$mulFun','$biopro','$ptm','$biosite','$ortho', $setID)";
        $doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
        //print "$insert\n";

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
