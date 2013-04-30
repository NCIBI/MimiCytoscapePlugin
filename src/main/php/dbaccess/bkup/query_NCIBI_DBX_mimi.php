<?
//Written by Jing Gao
//This script is called by mimi cytoscape plugin to query mimi database on 
//NCIBIDBX
$ids=$_POST['ID'];
$retrieve=$_POST['RETRIEVE'];
$organismID=$_POST['ORGANISMID'];
$moleculeType=$_POST['MOLECULETYPE'];
$dataSource=$_POST['DATASOURCE'];

//connect to ncibidbx
$server="dbx.ncibi.org";
$user="ncibiuser";
$pwd="GoBlue!";
$db="mimi";
$ret=" ";

$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
mssql_select_db( $db);


//get regular molecueid and mimi name
if($retrieve==-1){
        $queryNames="select  moleculeID ,min(idvalue)  from moleculename where moleculeid in ($ids) and idType='name' group by moleculeID";
        $resulNames=mssql_query($queryNames)or die("Sorry, \"$queryNames\" failed.");
        while ($myrow = mssql_fetch_array($resulNames))
                $ret.=$myrow[0]."/////".$myrow[1]."\n";
        print $ret;
}

//get moleculeid and gene name
if($retrieve==0){
	$queryNames="SELECT moleculeID, symbol FROM MoleculeGene WHERE moleculeID IN ($ids)";
	$resulNames=mssql_query($queryNames)or die("Sorry, \"$queryNames\" failed.");
	while ($myrow = mssql_fetch_array($resulNames))
		$ret.=$myrow[0]."/////".$myrow[1]."\n";
	print $ret;
}

//get  molecule and description hash
if($retrieve==1){
	 $queryDescription="SELECT moleculeID, description FROM MoleculeDescription WHERE moleculeID IN ($ids)";
        $resulNames=mssql_query($queryDescription)or die("Sorry, \"$queryDescription\" failed.");
        while ($myrow = mssql_fetch_array($resulNames)){
		$desc=preg_replace ('/\n/'," ",$myrow[1]);
                $ret.=$myrow[0]."/////".$desc."\n";
        }
        print $ret;

}

//get moleculeID1 moleculeId2 from special data sources
if ($retrieve==2){
	$queryIneractions="SELECT i.moleculeID1, i.moleculeID2, i.interactionRef,ip.imID FROM MoleculeInteraction i, InteractionProv ip WHERE i.moleculeID1 IN ($ids) AND i.interactionRef=ip.interactionRef AND ip.imID IN(40,70,80)";
	$resultIneractions=mssql_query($queryIneractions)or die("Sorry, \"$queryIneractions\" failed.");
        while ($myrow = mssql_fetch_array($resultIneractions)){
                $ret.=$myrow[0]."\t".$myrow[1]."\t".$myrow[2]."\t".$myrow[3]."\n";
        }
        print $ret;

}

?>
