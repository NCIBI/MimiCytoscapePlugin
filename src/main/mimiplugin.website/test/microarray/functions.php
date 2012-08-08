<?php

//connect to database server
//function conn_db($user="bionlp", $pwd="bionlp04", $db="microarray",$server="DB3"){
//function conn_db($user="ncibiuser", $pwd="GoBlue!", $db="microarray",$server="ncibidbx"){
function conn_db($user="userMicroarray", $pwd="u999Micr0", $db="microarray",$server="ncibidb"){

   $connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
   mssql_select_db( $db,$connection);
}

    
//generate platform using species as input
function getPlatform ($species){
switch ($species) {
case 1:
   $platform=$_POST['platformHuman'];
   break;
case 2:
   $platform=$_POST['platformMouse'];
   break;
case 3:
   $platform=$_POST['platformRat'];
   break;
}

return $platform;
}



function testQuery($query){
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");
$myrow="";
if (!($myrow = mssql_fetch_array($result))){
   print "No result for \"$query\"";
   exit;
}
$returnValue ="'".$myrow[0];
while ($myrow = mssql_fetch_array($result)){
   $returnValue.= "','" . $myrow[0];
}
$returnValue.="'";
return $returnValue;
}


//display microarray profile across tissues of genes looking up by symbol
function displayMicroarrayWithProvider($provider, $species, $platform, $genes,$sampleIDs){

conn_db();

//convert genes  gene1, gene2, gene3 =>'gene1','gene2','gene3'
$gene="'";
$geneArr=preg_split("/[\s,]+/",$genes);
for($i=0; $i<count($geneArr)-1; $i++){
   $gene.=$geneArr[$i]."','";
}
$gene.= $geneArr[$i]."'";

//test query step by step   
$query ="SELECT platformID FROM Platform WHERE platformName ='$platform'";
$platformID = testQuery($query);
$query="SELECT taxID FROM gene.dbo.TaxName WHERE name ='$species'";
$taxID=testQuery($query);
$query ="SELECT geneID FROM gene.dbo.Gene WHERE symbol IN ($gene) AND taxID IN ($taxID)";
$geneID=testQuery($query);
$query="SELECT ensemblGeneID FROM EnsemblEntrez WHERE entrezGeneID IN ($geneID)";
$ensemblGeneID=testQuery($query);
$query="SELECT probeName FROM PlatformGeneProbe WHERE  platformName ='$platform' AND  ensemblGeneID IN ($ensemblGeneID)";
$probeName=testQuery($query);
//$query="SELECT sampleID FROM SAMPLE WHERE platformID = $platformID AND seriesid in (SELECT  seriesid  FROM SERIES WHERE  seriesName like '%".$provider ."%')";
if ($sampleIDs==""){
    $query="SELECT sampleID FROM SAMPLE WHERE platformID = $platformID AND seriesid in (SELECT  seriesid  FROM SERIES WHERE  seriesName ='".$provider ."')";
}
else{
     $query="SELECT sampleID FROM SAMPLE WHERE platformID = $platformID AND sampleID IN (".$sampleIDs.")";
}
$sampleID=testQuery($query);
$query = "SELECT datasetID FROM SamplePlatformMethod WHERE platformID=$platformID AND methodID =1 AND sampleID IN ($sampleID)";
$datasetID = testQuery($query);

//prepare to display microarray profile
$dispStr = "<center><h2><font color =navy>Microarray Expression Profile for ".$provider."</font></h2><table cellspacing=1  border=1><th bgcolor=white>Tissue</th><th>Symbol</th><th>Data Set</th><th bgcolor=white>Probeset</th><th bgcolor=white>Signal</th><th bgcolor=white>Rank</th><th>Quantile Signal</th><th>Organism</th><th>Description</th>\n";

//execute query  get tissue, probeset, signal and symbol information using inputs provider, species, platform and symbol
$query ="SELECT sa.tissueOrCelllineName,gg.symbol, sa.sampleName, pgp.probeName, ps.signal, ps.intRank, ps.qnlSignal, sa.organism, sa.Description FROM Sample sa, Series se, gene.dbo.TaxName gtn, gene.dbo.gene gg, EnsemblEntrez ee,  PlatformGeneProbe pgp, SamplePlatformMethod spm, ProcessedSignal ps WHERE gg.symbol IN ($gene) AND gg.taxID = gtn.taxID AND gtn.name ='$species' AND gg.geneID =ee.entrezGeneID AND ee.ensemblGeneID =pgp.ensemblGeneID AND pgp.platformName ='$platform' AND sa.seriesID =se.seriesID AND se.seriesName = '".$provider."' AND sa.platformID =pgp.platformID AND spm.sampleID =sa.sampleID and spm.methodID =1 AND spm.platformID = pgp.platformID AND ps.datasetID = spm.datasetID AND ps.probeID = pgp.probeID AND sa.sampleID IN ($sampleID) ORDER BY sa.tissueOrCelllineName";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");

while ($myrow = mssql_fetch_array($result)){
      $dispStr.="<tr> <td>". $myrow[0]."</td><td>".$myrow[1]."</td><td>".$myrow[2]."</td><td>".$myrow[3]."</td><td>".$myrow[4]."</td><td>".$myrow[5]."</td><td>".$myrow[6]."</td><td>".$myrow[7]."</td><td>".$myrow[8]."</td></tr>";
}
 
print "$dispStr";
}

function dispAllSeries($curPos, $pages){
   conn_db();
   $currentPage =$curPos/20+1 ;
   $stm= "Page ". $currentPage." of " . $pages ." <br>"; 
   print $stm;
   $cursor=mssql_query ("DECLARE sent_cursor SCROLL CURSOR FOR SELECT seriesName , seriesID , pubmedID, laboratory,investigator, sampleSize, submissionDate  FROM Series ORDER BY seriesName");
   $cursor = mssql_query("OPEN sent_cursor");
   $cur=$cursor;
   $i=0;
   $retStr="<table border='0'><tr><td ><></tr>";
   $retStr="<center><h2><font color='navy'>All Series in Microarray Database</font></h2><table width='100%' border='0'><tr><td bgcolor='EEEEEE'><strong>Name</strong></td><td bgcolor='#eeffee'><strong>Laboratory</strong></td><td bgcolor='EEEEEE'><strong>Investigator</strong></td><td bgcolor='#eeffee'><strong>SampleSize</strong></td><td bgcolor='EEEEEE'><strong>Submision Date</strong></td><td bgcolor='#eeffee'><strong>Pubmed ID</strong></td></tr>";
   while ($i<20){
     $cursor = mssql_query("FETCH ABSOLUTE $curPos FROM sent_cursor");
     $curPos++;
     $i++;
     $row = mssql_fetch_array($cursor);
     $seriesName = $row[0];
     $seriesID = $row[1];
     $pubID=  $row[2];
     $lab=  $row[3];
     $investigator=  $row[4];
     $samp=  $row[5];
     $subDate=  $row[6];
     $retStr.= "<tr><td bgcolor='EEEEEE'><strong>".$seriesName."</strong></td><td bgcolor='#eeffee'><strong>".$lab."</strong></td><td bgcolor='EEEEEE'><strong>".$investigator."</strong></td><td bgcolor='#eeffee'><strong>".$samp."</strong></td><td bgcolor='EEEEEE'><strong>".$subDate."</strong></td><td bgcolor='#eeffee'><strong>".$pubID."</strong></td></tr>";
   }
   print  $retStr;
   return $curPos;

}

function dipSearchResult($text){
    conn_db();
    $str="";
    $qryReslt=mssql_query ("SELECT seriesName, seriesID FROM Series WHERE seriesName like'%".$text."%'");
    while ($row = mssql_fetch_array($qryReslt)){
        $seriesName = $row[0];
        $seriesID = $row[1];
        $str.="<div style=\"visibility:visible\"  class=\"parent\" id =\" " . $seriesID . "\"><a href=\"javascript:;\" onClick=\"showSample('".$seriesID."');return false;\">".$seriesName."</a></div>";
        $str.= "<div  style=\"display:none\" class = \"child\" id =\"sample".$seriesID."\">";
        $str.="<form  name =\"formInFunc".$seriesID."\"><INPUT type=\"checkbox\" name =\"checkbox".$seriesID."\" onClick=\"selectAll(this,'".$seriesID."')\">Select All<br>";
        $smplRlt=mssql_query ("SELECT sampleName, sampleID FROM Sample WHERE seriesid ='".$seriesID."'");
        while ($row1=mssql_fetch_array($smplRlt)){
             $str.=  "<INPUT type=\"checkbox\" name =\"checkbox".$seriesID."\" value=\"".$row1[1]."\" >".$row1[0]."<br>";
        }
        $str.= "<INPUT type =\"submit\" value=\"Submit To Main Menu\" onClick=\"send2Main('".$seriesName."','".$seriesID."');\"> <INPUT type= reset> </form></div>";
    }
    print  $str;
}

function getPages(){
   conn_db();   
   $query="SELECT COUNT(*) FROM Series";
   $result=mssql_query($query)or die("Sorry, \"$query\" failed.");
   $myrow = mssql_fetch_array($result);
   $totalSeries= $myrow[0];
   $pages=0;
   if (($totalSeries % 20)!= 0){$pages=($totalSeries-$totalSeries % 20)/20 +1;}
   else {$pages=$totalSeries /20; }     
   return array($pages,$totalSeries);
}


//display microarray profile across tissues of genes looking up by symbol
function  showMicrProfileWithGenes($species, $platform, $genes,$sampleIDs, $seriesName,$sid){

conn_db();

//convert genes  gene1, gene2, gene3 =>'gene1','gene2','gene3'
$gene="";
$genes=trim($genes);
$geneArr=preg_split("/[\s,]+/",$genes);
for($i=0; $i<count($geneArr); $i++){
   $gene.="'".$geneArr[$i]."',";
}
$gene= substr($gene,0,-1);

//test query step by step
$query ="SELECT platformID FROM Platform WHERE platformName ='$platform'";
$platformID = testQuery($query);
$query="SELECT taxID FROM gene.dbo.TaxName WHERE name ='$species'";
$taxID=testQuery($query);
$query ="SELECT geneID FROM gene.dbo.Gene WHERE symbol IN ($gene) AND taxID IN ($taxID)";
$geneID=testQuery($query);
$query="SELECT ensemblGeneID FROM EnsemblEntrez WHERE entrezGeneID IN ($geneID)";
$ensemblGeneID=testQuery($query);
$query="SELECT probeName FROM PlatformGeneProbe WHERE  platformName ='$platform' AND  ensemblGeneID IN ($ensemblGeneID)";
$probeName=testQuery($query);
if ($sampleIDs==""){
    $query="SELECT sampleID FROM SAMPLE WHERE platformID = $platformID AND seriesid ='".$sid ."'";
}
else{
     $query="SELECT sampleID FROM SAMPLE WHERE platformID = $platformID AND sampleID IN (".$sampleIDs.")";
}
$sampleID=testQuery($query);
$query = "SELECT datasetID FROM SamplePlatformMethod WHERE platformID=$platformID AND methodID =1 AND sampleID IN ($sampleID)";
$datasetID = testQuery($query);

//prepare to display microarray profile
$dispStr = "<center><h2>Microarray Expression Profile for <font color='navy'>".$seriesName."</font></h2><table  width='100%'><tr><th bgcolor='#EEEEEE'><strong>Tissue</strong></th><th bgcolor='#eeffee'><strong>Symbol</strong></th><th  bgcolor='#EEEEEE'><strong>Data Set</strong></th><th  bgcolor='#eeffee'><strong>Probeset</th><th bgcolor='#EEEEEE'><strong>Signal</strong></th><th bgcolor='#eeffee'><strong>Rank</strong></th><th bgcolor='#EEEEEE'><strong>Quantile Signal</strong></th><th bgcolor='#eeffee'>Organism</th></tr>";

//execute query  get tissue, probeset, signal and symbol information using inputs seriesid, species, platform and symbol
$query ="SELECT sa.tissueOrCelllineName,gg.symbol, sa.sampleName, pgp.probeName, ps.signal, ps.intRank, ps.qnlSignal, sa.organism FROM Sample sa, Series se, gene.dbo.TaxName gtn, gene.dbo.gene gg, EnsemblEntrez ee,  PlatformGeneProbe pgp, SamplePlatformMethod spm, ProcessedSignal ps WHERE gg.symbol IN ($gene) AND gg.taxID = gtn.taxID AND gtn.name ='$species' AND gg.geneID =ee.entrezGeneID AND ee.ensemblGeneID =pgp.ensemblGeneID AND pgp.platformName ='$platform' AND sa.seriesID= se.seriesID AND sa.seriesID ='".$sid."' AND sa.platformID =pgp.platformID AND spm.sampleID =sa.sampleID and spm.methodID =1 AND spm.platformID = pgp.platformID AND ps.datasetID = spm.datasetID AND ps.probeID = pgp.probeID AND sa.sampleID IN ($sampleID) ORDER BY sa.sampleName";
$result=mssql_query($query)or die("Sorry, \"$query\" failed.");

while ($myrow = mssql_fetch_array($result)){
      $dispStr.="<tr style='font:smaller'> <td bgcolor='#EEEEEE'>". $myrow[0]."</td><td  bgcolor='#eeffee'>".$myrow[1]."</td><td bgcolor='#EEEEEE'>".$myrow[2]."</td><td bgcolor='#eeffee'>".$myrow[3]."</td><td bgcolor='#EEEEEE'>".$myrow[4]."</td><td bgcolor='#eeffee'>".$myrow[5]."</td><td  bgcolor='#EEEEEE'>".$myrow[6]."</td><td bgcolor='#eeffee'>".$myrow[7]."</td></tr>";
}

print "$dispStr";
}




function searchDataSet($keyword){
   conn_db();
   //no matter normalized method
   //$query1="SELECT distinct se.seriesID, se.seriesName FROM Sample sa, Series se WHERE (sa.description like '%".$keyword."%' OR se.description like '%".$keyword."%' OR se.seriesName like '%".$keyword."%' OR sa.sampleName like '%".$keyword."%' OR sa.tissueOrCelllineName like'%".$keyword."%') AND se.seriesID = sa.seriesID ORDER BY se.seriesName DESC";

   //only RMA method, sampleplatformmethod. methodid =1
   $query1="SELECT distinct se.seriesID, se.seriesName FROM Sample sa, Series se, SamplePlatformMethod spm WHERE (sa.description like '%".$keyword."%' OR se.description like '%".$keyword."%' OR se.seriesName like '%".$keyword."%' OR sa.sampleName like '%".$keyword."%' OR sa.tissueOrCelllineName like'%".$keyword."%') AND se.seriesID = sa.seriesID AND spm.sampleID =sa.sampleID AND spm.methodID=1 ORDER BY se.seriesName DESC";
   $result=mssql_query($query1)or die("Sorry, \"$query1\" failed.");
   if (!($myrow = mssql_fetch_array($result))){
       $str="<table width= '100%' bgcolor='#ffcccc'><tr><td>The following term was not found:<font color='#0000FF'>".$keyword.".</font><br>Query not found </td></tr></table>";
       print $str;
       exit;
   }
?>
<html>
<head>
<script language=javascript>
function selectAllSamples(obj,sid){
   var checkedOrNo = obj.checked;
   var checkbox="selectSamples"+sid+"[]";
   var col = document.getElementsByName(checkbox);
   for (var i=0;i<col.length;i++) {
       col[i].checked= checkedOrNo;
   }

}
function checkGenes(sid){
    genefieldid="genetext"+sid;
    uploadfieldid="uploadFile"+sid;
    obj=document.getElementById(genefieldid);
    obj1=document.getElementById(uploadfieldid);
    empty=new RegExp(/^\s*$/);
    if (((obj.value=="")||(obj.value.match(empty)))&&((obj1.value=="")||(obj1.value.match(empty)))){
        alert("gene area could not be empty.");
        return false;
    }
    else{ return true;}
}
</script>
</head>
<?
   $seriesids =array();
   $seriesids[0]=$myrow[0];
   $i=1;   
   while ($myrow = mssql_fetch_array($result)){
          $seriesids[$i]=$myrow[0];
          $i++;
   }

   $str="<body link='blue' VLINK='blue' ALINK='blue'> <table width='100%' border='0'>";
   foreach ($seriesids as $sid){
           $querySe="SELECT * FROM Series WHERE seriesID ='".$sid."'";
           $resultSe=mssql_query($querySe)or die("Sorry, \"$querySe\" failed.");
           $rowSe = mssql_fetch_array($resultSe);
           $pubmedid=$rowSe[2]; 
           $laboratory=$rowSe[3];
           $seriesName=$rowSe[5];
           $sampleSize=$rowSe[8];
           $description=$rowSe[9];
           //seriesid => organism 
           $queryOrg="SELECT distinct p.organism FROM Platform p, Sample sa WHERE sa.platformid =p.platformid AND sa.seriesid ='".$sid."'";
           $resultOrg=mssql_query($queryOrg)or die("Sorry, \"$queryOrg\" failed.");
           $rowOrg = mssql_fetch_array($resultOrg);
           $org=$rowOrg[0];
           //seriesid=>platforms
           $queryPF="SELECT platformName, gplName, platformID FROM Platform WHERE platformID IN (SELECT distinct platformID FROM Sample WHERE seriesID ='".$sid."')";
           $resultPF=mssql_query($queryPF)or die("Sorry, \"$queryPF\" failed.");
           //prepare display content
           $str.="<tr bgcolor='#DDDDDD'><td colspan='7'><font color='#008080' size=+1>".$seriesName."&nbsp;&nbsp;[".$org."]</font></td></tr><tr><td>&nbsp;</td></tr><tr><td valign='top' width='94'>Summary: </td><td colspan='6'>".$description."<br/><font color='#99000000'><font color='brick'>Platform(s):</font>&nbsp;</font>";
           $platformNameArr=array();
           $platformIDArr=array();
           $j=0;
           while($rowPF= mssql_fetch_array($resultPF)){
                 $rowPF[1]=trim($rowPF[1]);
                 $str.="<a href='http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=".$rowPF[1]."' style='text-decoration:none'>".$rowPF[1]."</a>&nbsp;[".$rowPF[0]."]&nbsp;&nbsp;&nbsp;&nbsp;";
                 $platformNameArr[$j]=$rowPF[0];
                 $platformIDArr[$j]=$rowPF[2];
                 $j++;
           }
           $str.="<br/><font color='#990000'><font color='brick'>PMID:</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font><a style='text-decoration:none' href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?CMD=search&DB=pubmed&term=".$pubmedid."'>".$pubmedid."</a></td></tr><tr><td valign='top' width='94'> Laboratory:</td><td colspan='6'>".$laboratory."</td></tr><tr><td valign='top'width='94'> Sample: </td><td colspan='6'>".$sampleSize."</td></tr>";
          //get samples
          $querySa="SELECT sampleName, sampleID FROM Sample WHERE seriesID ='$sid' ORDER BY sampleName, sampleID";
          $resultSa=mssql_query($querySa)or die("Sorry, \"$querySa\" failed.");
          $i=1;
          $str.="<tr><td width=94>&nbsp;</td>";
          //form for query genes with a series and user selected samples 
          $str.="<form name='formInSearch2".$sid."' enctype='multipart/form-data'  method='post' action='queryGene.php' target='_blank'>";
          while ($rowSa= mssql_fetch_array($resultSa)){
                 //samplename=>title, subtype, subtype description
                 $samplehintinfor="";
                 $sampleDescription ="";
                 $querySampleSubsetGds="SELECT description, type, typeDescription FROM SampleSubsetGds WHERE sampleName='".$rowSa[0]."'";
                 $restulSampleSubsetGds=mssql_query($querySampleSubsetGds)or die("Sorry, \"$querySampleSubsetGds\" failed.");
                 while($rowssg= mssql_fetch_array($restulSampleSubsetGds)){
                       $samplehintinfor.="Type: ".$rowssg[1]."\nDescription: ".$rowssg[2]."\n\n";
                       $sampleDescription =$rowssg[0];
                 }
                 $samplehintinfor="Title: ".$sampleDescription."\n\n".$samplehintinfor;
                 if ($i%6==0){
                     $str.="<td><font size=-2><input type='checkbox' name='selectSamples".$sid."[]' value='".$rowSa[1]."'><a href='searchSample.php?sampleName=".$rowSa[0]."' title='".$samplehintinfor."' style='text-decoration:none' >".$rowSa[0]."</a></font></td></tr><tr><td width=94>&nbsp;</td>";
                     $i++;
                 }
                 else { 
                     $str.="<td><font size=-2><input type='checkbox' name='selectSamples".$sid."[]' value='".$rowSa[1]."'><a href='searchSample.php?sampleName=".$rowSa[0]."' title='".$samplehintinfor."' style='text-decoration:none'>".$rowSa[0]."</a></font></td>";
                     $i++;
                 }
          }
          //create gene text box
          $str.="<tr><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td width=94><a href='' style='text-decoration:none'>Genes</a></td><td colspan='6'><textarea name='genetextarea' ID='genetext".$sid."' cols='60' rows='6'></textarea></td></tr><tr><td width='94'>&nbsp;</td><td colspan='6'>Upload Gene File From Local: <input type='file' name='uploadFile".$sid."' id='uploadFile".$sid."'></td></tr><tr><td>&nbsp;</td></tr><tr><td width='94'>Platform</td><td colspan='6'><select name='platformName'>";
          $str.="<option selected>".$platformNameArr[0]."</option>";
          for($k=1; $k<count($platformNameArr);$k++){
               $str.="<option>".$platformNameArr[$k]."</option>";
          }
          $str.="</select></td></tr><tr><td width='94' valign='top'>Sample(s)</td><td colspan='6'>Click above check box to choose desired SAMPLES<strong> OR </strong><br/><input type='checkbox' name='selectAllsamples".$sid."' onClick='selectAllSamples(this,\"".$sid."\")'>Select All Samples</td></tr><tr><td width='94'>&nbsp;</td><td colspan='6'><input type='submit' name='gobutton' id='gobutton' value='Query for Genes...' onClick='return checkGenes(\"$sid\")'></td></tr><tr><td>&nbsp;</td><input type='hidden' name='seriesID' value='".$sid."'><input type='hidden' name='species' value='".$org."'><input type='hidden' name='seriesName' value='".$seriesName."'><tr><td width='200'><a href='http://www.bioinformatics.med.umich.edu/app/nlp/microarray/rma/".$seriesName.".zip' >Down Load Normalized File here.</a></td><td>&nbsp;</td></tr></form>";
   }//foreach loop 
   $str.="</table>";
   print $str;
}


?>
