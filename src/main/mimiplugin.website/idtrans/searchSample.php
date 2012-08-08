<?
   include('functions.php');
   conn_db();
   $sampleName=$_GET['sampleName'];
   //get sample infor
   $querySa="SELECT title, tissueOrCelllineName, normalOrDisease, description,organism, sourceName, molecule FROM Sample WHERE sampleName='".$sampleName."'";
   $resultSa= mssql_query($querySa)or die ("Sorry, \"$querySa\" failed.");
   $rowSa=mssql_fetch_array($resultSa);
   $title=trim($rowSa[0]);
   $tissueOrCelllineName=trim($rowSa[1]);
   $normalOrDisease=trim($rowSa[2]);
   $descriptionSa=trim($rowSa[3]);
   $organism= trim($rowSa[4]);
   $sourceName=trim($rowSa[5]);
   $molecule=trim($rowSa[6]);
   //get series infor
   $querySe="SELECT investigator, type, submissionDate, webLink,contributor,contactName, contactEmail,contactPhone,department,institute, address,city FROM Series WHERE seriesId IN (SELECT seriesId FROM Sample WHERE sampleName='".$sampleName."')";
   $resultSe= mssql_query($querySe)or die ("Sorry, \"$querySe\" failed.");
   $rowSe=mssql_fetch_array($resultSe);
   $investigator=trim($rowSe[0]);
   $type=trim($rowSe[1]);
   $submissionDate=trim($rowSe[2]);
   $weblink=trim($rowSe[3]);
   $contributor=trim($rowSe[4]);
   $contactName=trim($rowSe[5]);
   $contactEmail=trim($rowSe[6]);
   $contactPhone=trim($rowSe[7]);
   $department=trim($rowSe[8]);
   $institute = trim($rowSe[9]);
   $address=trim($rowSe[10]);
   $city=trim($rowSe[11]);
   //organism=>taxId
   $queryTa="SELECT taxID FROM Gene.dbo.TaxName WHERE name='".$organism."'";
   $resultTa=mssql_query($queryTa)or die ("Sorry, \"$queryTa\" failed.");
   $rowTa=mssql_fetch_array($resultTa);
   $taxID=$rowTa[0];
   //samplename=>platformName
   $queryPn="SELECT p.platformName, p.gplName FROM Platform p, Sample sa WHERE sa.sampleName='".$sampleName."' AND sa.platformId = p.platformId";
   $resultPn=mssql_query($queryPn)or die ("Sorry, \"$queryPn\" failed.");
   $rowPn=mssql_fetch_array($resultPn);
   $platformName=$rowPn[0];
   $gplName=trim($rowPn[1]);
   
   //prepare display content
   $str="<body vlink='blue' alink='blue'><table border='0' width='100%'><tr><td bgcolor='#DDDDDD' colspan='2'><font color='#008080' size=+1>Sample&nbsp;&nbsp;&nbsp;".$sampleName."</font></td></tr>";
  
   if (!(empty($descriptionSa)) && !(ctype_space($descriptionSa))){ 
         $str.="<tr><td width='15%' valign='top'>Description</td><td width='85%'>".$descriptionSa."</td></tr>"; 
   }
   if (!(empty($title)) && !(ctype_space($title))){
        $str.="<tr><td width='15%' valign='top'>Title</td><td>".$title."</td></tr>";
   }
   if (!(empty($type)) && !(ctype_space($type))){
         $str.="<tr><td width='15%' valign='top'>Sample Type</td><td>".$type."</td></tr>";
   }
   $str.="<tr><td width='15%' valign='top'>&nbsp;</td><td>&nbsp;</td></tr>";
   if (!(empty($sourceName)) && !(ctype_space($sourceName))){
        $str.="<tr><td width='15%' valign='top'>Source Name</td><td width='85%'>".$sourceName."</td></tr>";
   }
   if (!(empty($organism)) && !(ctype_space($organism))){
        $str.="<tr><td width='15%' valign='top'>Organism</td><td><a href='http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=".$taxID."' style='text-decoration:none'>".$organism."</a></td></tr>";
   }
   if (!(empty($tissueOrCelllineName)) && !(ctype_space($tissueOrCelllineName))){
        $str.="<tr><td width='15%' valign='top'>Tissue/Cell line</td><td>".$tissueOrCelllineName."</td></tr>";
   }
   if (!(empty($molecule)) && !(ctype_space($molecule))){
         $str.="<tr><td width='15%' valign='top'>Molecule</td><td>".$molecule."</td></tr>";
   }
   $str.="<tr><td width='15%' valign='top'>Platform</td><td><a href='http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=".$gplName."' style='text-decoration:none'>".$gplName."</a>&nbsp;<font color='brick'>[".$platformName."]</font></td></tr>";
   $str.="<tr><td width='15%' valign='top'>&nbsp;</td><td>&nbsp;</td></tr>";
   if (!(empty($submissionDate)) && !(ctype_space($submissionDate))){ 
         $str.="<tr><td width='15%' valign='top'>submission Date</td><td>".$submissionDate."</td></tr>";
   }
   if (!(empty($contactName)) && !(ctype_space($contactName))){
         $str.="<tr><td width='15%' valign='top'>Contact Name</td><td>".$contactName."</td></tr>";
   }
   if (!(empty($contactPhone)) && !(ctype_space($contactPhone))){
        $str.="<tr><td width='15%' valign='top'>Phone</td><td>".$contactPhone."</td></tr>";
   }
   if (!(empty($contactEmail)) && !(ctype_space($contactEmail))){
        $str.="<tr><td width='15%' valign='top'>Email</td><td>".$contactEmail."</td></tr>";
   }
   if (!(empty($weblink)) && !(ctype_space($weblink))){
        $str.="<tr><td width='15%' valign='top'>URL</td><td><a href='".$weblink."' style='text-decoration:none'>".$weblink."</a></td></tr>";
   }
   if (!(empty($department)) && !(ctype_space($department))){
        $str.="<tr><td width='15%' valign='top'>Department</td><td>".$department."</td></tr>";
   }
   if (!(empty($institute)) && !(ctype_space($institute))){
        $str.="<tr><td width='15%' valign='top'>Institute</td><td>".$institute."</td></tr>";
   }
   if (!(empty($address)) && !(ctype_space($address))){
        $str.="<tr><td width='15%' valign='top'>Address</td><td>".$address."</td></tr>";
   }
   if (!(empty($city)) && !(ctype_space($city))){
        $str.="<tr><td width='15%' valign='top'>City</td><td>".$city."</td></tr>";
   }
   $str.="<tr><td width='15%' valign='top'>&nbsp;</td><td>&nbsp;</td></tr>";
   $str.="</table>";
   //prepare  data table content
   $str.="<table width='100%'><tr><td bgcolor='EEEEEE' colspan='4'><strong><font size=+1>Data table</font></strong></td></tr><tr><td bgcolor='EEEEEE'><strong>Probe ID</strong> </td><td bgcolor='#eeffee'><strong>Gene Name</strong></td><td bgcolor='EEEEEE'><strong>NCBI GeneID</strong> </td><td bgcolor='#eeffee'><strong>Value</strong></td><td bgcolor='EEEEEE'><strong>Rank</strong></td><td bgcolor='#eeffee'><strong>Quantile Signal</strong></td></tr>";
   //get probe, genename, ncbi geneid  and signal infor
   $querySig="SELECT TOP 20 p.probeName,gds_pgp.geneName, gds_pgp.entrezGeneID, ps.signal, ps.intRank, ps.qnlSignal FROM ProcessedSignal ps, SamplePlatformMethod spm, Sample sa, PlatformProbe p,PlatformGeneProbeGds gds_pgp, Platform pf WHERE sa.sampleName='".$sampleName."' AND sa.sampleID=spm.sampleID AND spm.methodID=1 AND ps.dataSetID=spm.dataSetID AND p.probeId =ps.probeID AND p.platformID =pf.platformID AND pf.gplName = gds_pgp.gplName AND p.probeName=gds_pgp.probeName";
   $resultSig=mssql_query($querySig)or die ("Sorry, \"$querySig\" failed.");
   while ($rowSig=mssql_fetch_array($resultSig)){
          $probeName=$rowSig[0];
          $geneName= $rowSig[1];
          $entrezGeneID =$rowSig[2];
          $signal=$rowSig[3];
          $intRank=$rowSig[4];
          $qnlSignal=$rowSig[5];
          $str.="<tr><td bgcolor='EEEEEE'><font size=-1>".$probeName."</font></td><td bgcolor='#eeffee'><font size=-1><a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=".$geneName."' style='text-decoration:none'>".$geneName."</a></font></td><td bgcolor='EEEEEE'><font size=-1><a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=".$entrezGeneID."' style='text-decoration:none'>".$entrezGeneID."</a></font></td><td bgcolor='#eeffee'><font size=-1>".$signal."</font></td><td bgcolor='EEEEEE'><font size=-1>".$intRank."</font></td><td bgcolor='#eeffee'><font size=-1>".$qnlSignal."</font></td></tr>";
   }
   $str.="</table><br><br>";
   //get total number of rows for table
   $queryTl="SELECT count (ps.probeid)  FROM ProcessedSignal ps, SamplePlatformMethod spm, Sample sa WHERE sa.sampleName='".$sampleName."' AND sa.sampleID=spm.sampleID AND spm.methodID=1 AND ps.dataSetID=spm.dataSetID";
   $resultTl=mssql_query($queryTl)or die ("Sorry, \"$queryTl\" failed.");
   $rowTl=mssql_fetch_array($resultTl);
   $totalrows=$rowTl[0];
   $str.="Table Truncated..., total number of rows: <strong>".$totalrows."</strong><br><br>";
   //prepare full table content
   $str.="<input type='submit' name='submit' value='Display Full Table...' onClick='window.open(\"displayFullTable.php?sampleName=".$sampleName."&toatlRows=".$totalrows."\", \"_blank\")'>";
   print $str;
   
   
?>
