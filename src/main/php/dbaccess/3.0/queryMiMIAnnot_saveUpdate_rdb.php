<?
include ('function.php');
//This script is called by mimi cytoscape plugin to save annotaion to NodeAnnot or EdgeAnnot table on MiMIAnnotation NCIBI-DBX server
$table=$_POST['TABLE'];
$ID=$_POST['ID'];
$setName =$_POST['SETNAME'];
$userid=$_POST['USERID'];
$annot=$_POST['ANNOTATION'];
$urls=$_POST['URLS'];
$share=$_POST['SHARE'];
$db="MiMIAnnotation";
conn_db_more();
mssql_select_db( $db);
$tableSet="R2.".$table."AnnotSet";
$tableAnnot="R2.".$table."Annot";
$tableUrl="R2.".$table."URLs";

if (strcmp($table,"Node")==0){
	$geneName =$_POST['GENENAME'];
	$alias=$_POST['ALIAS'];
	$chromosome=$_POST['CHROMOSOME'];
        $maploc =$_POST['MAPLOC'];
        $locustag=$_POST['LOCUSTAG'];
	$taxid=$_POST['TAXID'];
	$taxonomyName=$_POST['TAXNAME'];
	$type = $_POST['TYPE'];
	$description=$_POST['DESCRIPTION'];
	$comp=$_POST['COMPONENT'];
	$fun=$_POST['FUNCTION'];
	$proc=$_POST['PROCESS'];
	$kegg=$_POST['KEGG'];
	$pathway=$_POST['PATHWAY'];
	$complex=$_POST['COMPLEX'];
	$addSet="exec R2.addnodeset $ID,'$setName',$userid,$share";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
	$getsetID="exec R2.nodeset $ID,'$setName',$userid";	
	$dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
	if ($myrow = mssql_fetch_array($dogetsetID)){
		$setID=$myrow[0];
		$insert="exec R2.addnodeannot $setID,$ID,'$geneName','$chromosome','$locustag','$maploc','$alias','$type','$taxonomyName','$taxid','$description','$comp','$fun','$proc','$kegg','$pathway','$complex','$annot'";
        $doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
        //print "$insert\n";

	}
}


if (strcmp($table,"Edge")==0){
	$intGenename=$_POST['INTGENENAME'];		
	$type=$_POST['TYPE'];              
	$prov=$_POST['PROVENANCE'];
	$comp=$_POST['COMPONENT'];
	$func=$_POST['FUNCTION'];
	$process=$_POST['PROCESS'];
	$pubmed=$_POST['PUBMED'];
	$addSet="exec R2.addedgeset '$ID','$setName',$userid,$share";
        mssql_query($addSet);//or die("Sorry, \"$addSet\" failed.");
	$getsetID="exec R2.edgeset '$ID','$setName',$userid";
        $dogetsetID=mssql_query($getsetID);//or die("Sorry, \"$getsetID\" failed.");
        if ($myrow = mssql_fetch_array($dogetsetID)){
                $setID=$myrow[0];		
		$insert="exec R2.addedgeannot $setID,'$ID','$intGenename','$type','$comp','$func','$process','$prov','$pubmed','$annot'";
        	$doInsert=mssql_query($insert);//or die("Sorry, \"$insert\" failed.");
       }	
}
//update nodeurls or edgeurls tables 
//first delete old and then insert new 
for ($i=0;$i<count($urls);$i++){
	//print "url is ".$curUrl."\n";
	$curUrl=$urls[$i];
	$insertUrls ="exec R2.addurl '$tableUrl', $setID,'$curUrl'";
	mssql_query($insertUrls);// or die("Sorry,\"$insertUrls\" failed.");
}
mssql_close() 
?>
