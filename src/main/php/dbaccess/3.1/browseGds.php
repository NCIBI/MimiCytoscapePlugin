<?
include("function.php");
$geneList=$_GET['GENELIST'];
//print "[$geneList]";
$db="gds";
//conn_db_db3();
conn_db();
mssql_select_db($db);
$totalDatSet="";

$query="exec denorm.getGds4Gene '$geneList'";//denorm.browseGds"; 
$result=mssql_query($query) or die("$query failed\n");
if ($geneList==""){
	$query1="exec denorm.gdsCount";
	$result1=mssql_query($query1) or die("$query1 failed\n");
	if ($myrow1=mssql_fetch_array($result1))
        	$totalDatSet=$myrow1[0];
}
else{
	$totalDatSet=mssql_num_rows($result);
}
print "<HTML><BODY><CENTER><H1><FONT COLOR='navy'>GDS DATASET</FONT></H1></br><FONT COLOR='brick'><strong> $totalDatSet</strong> data sets found</FONT><TABLE width='70%' border='1' cellspacing='0'><TR bgcolor='eeeeee'><TD><STRONG>DatasetID</STRONG></TD><TD><STRONG>Type</STRONG></TD><TD><STRONG>Pubmed</STRONG></TD><TD><STRONG>Organism</STRONG></TD><TD><STRONG>SampleType</STRONG></TD><TD><STRONG>SampleCount</STRONG></TD><TD><STRONG>ValueType</TD><TD><STRONG>Update</STRONG></TD></TR>";

$count=0;	   
while ($myrow=mssql_fetch_array($result)){
	$bgcolor="EEFFFF";
	if ($count%2==0)$bgcolor="FFFFEE";
	$gdsNo=substr($myrow[0],3);
	//print"<TR bgcolor='eeeeee'><TD><STRONG>DatasetID</STRONG></TD><TD><STRONG>Type</STRONG></TD><TD><STRONG>Pubmed</STRONG></TD><TD><STRONG>Organism</STRONG></TD><TD><STRONG>SampleType</STRONG></TD><TD><STRONG>SampleCount</STRONG></TD><TD><STRONG>ValueType</TD><TD><STRONG>Update</STRONG></TD></TR>";
	print"<TR bgcolor='$bgcolor'><TD><A HREF='http://www.ncbi.nlm.nih.gov/geo/gds/gds_browse.cgi?gds=$gdsNo'>$myrow[0]</A></TD>";
	print"<TD>$myrow[3]</TD>";
	if ($myrow[4]=="")print "<TD>null</TD>";
	else print"<TD><A HREF='http://www.ncbi.nlm.nih.gov/sites/entrez?db=pubmed&cmd=search&term=$myrow[4]'>$myrow[4]</A></TD>";
	print"<TD>$myrow[5]</TD><TD>$myrow[6]</TD><TD>$myrow[7]</TD><TD>$myrow[8]</TD><TD>$myrow[9]</TD></TR>";
	print"<TR  bgcolor='$bgcolor'><TD colspan='8'><strong>Title:</strong>&nbsp;$myrow[1]<br><strong>Description:</strong>&nbsp;$myrow[2]</TD></TR>";
	$count++;
}
mssql_close();
?>
