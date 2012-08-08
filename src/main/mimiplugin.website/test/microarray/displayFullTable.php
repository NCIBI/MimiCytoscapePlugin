<?
   include('functions.php');
   conn_db();
   $sampleName=$_GET['sampleName'];
   $totalRows=$_GET['toatlRows'];
   $str="<center><h2>Data Table For <font color='navy'>". $sampleName."</font></h2>Total Rows: <font color='navy'><strong>".$totalRows."</strong></font></center>";

   $str .="<table width='100%'><tr><td bgcolor='EEEEEE' colspan='4'><strong><font size=+1>Data table</font></strong></td></tr><tr><td bgcolor='EEEEEE'><strong>Probe ID</strong> </td><td bgcolor='#eeffee'><strong>Gene Name</strong></td>><td bgcolor='EEEEEE'><strong>NCBI GeneID</strong> </td><td bgcolor='#eeffee'><strong>Value</strong></td><td bgcolor='EEEEEE'><strong>Rank</strong></td><td bgcolor='#eeffee'><strong>Quantile Signal</strong></td></tr>";
   $querySig ="SELECT p.probeName,gds_pgp.geneName, gds_pgp.entrezGeneID, ps.signal, ps.intRank, ps.qnlSignal FROM ProcessedSignal ps, SamplePlatformMethod spm, Sample sa, PlatformProbe p,PlatformGeneProbeGds gds_pgp, Platform pf WHERE sa.sampleName='".$sampleName."' AND sa.sampleID=spm.sampleID AND spm.methodID=1 AND ps.dataSetID=spm.dataSetID AND p.probeId =ps.probeID AND p.platformID =pf.platformID AND pf.gplName = gds_pgp.gplName AND p.probeName=gds_pgp.probeName";
   $resultSig=mssql_query($querySig)or die ("Sorry, \"$querySig\" failed.");
   while ($rowSig=mssql_fetch_array($resultSig)){
          $probeName=$rowSig[0];
          $geneName= $rowSig[1];
          $entrezGeneID =$rowSig[2];
          $signal=$rowSig[3];
          $intRank=$rowSig[4];
          $qnlSignal=$rowSig[5];
          $str.="<tr><td bgcolor='EEEEEE'><font size=-2>".$probeName."</font></td><td bgcolor='#eeffee'><font size=-1><a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=".$geneName."' style='text-decoration:none'>".$geneName."</a></font></td><td bgcolor='EEEEEE'><font size=-1><a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=search&term=".$entrezGeneID."' style='text-decoration:none'>".$entrezGeneID."</a></font></td><td bgcolor='#eeffee'><font size=-2>".$signal."</font></td><td bgcolor='EEEEEE'><font size=-2>".$intRank."</font></td><td bgcolor='#eeffee'><font size=-2>".$qnlSignal."</font></td></font></tr>";
   }
   print $str;
?>
