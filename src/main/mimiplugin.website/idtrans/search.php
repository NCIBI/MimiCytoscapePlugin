<?
  include('./functions.php');
  if ((empty($_POST['provider']))|| (ctype_space( $_POST['provider']))){
?>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
</head>

<body>
<table width="439" border="0" >
  <tr>
    <td width="330"   bgcolor="#6699FF"><font size=+2>DataSets</font></td>
  </tr>
</table>
<blockquote>
  <p>Microarray Database collects microarray data  from well known microarray gene expression warehouses and <a href="http://www.ncibi.org/"  style="text-decoration:none">NCIBI</a> collaborators  for biomedical researchers to query and analyze data in their studies. One of the  major sources to obtain microarray data is <a href="http://www.ncbi.nlm.nih.gov/geo/" style="text-decoration:none" > NCBI GEO</a>. We also acquire microarray  data from <a href="" style="text-decoration:none">CaArray</a>,<a href="" style="text-decoration:none"> EBI ArrayExpress</a>, <a href="" style="text-decoration:none" >DGAP</a> (Diabetes Genome Anatomy Project) and  <a href="" style="text-decoration:none">NCIBI collaborators</a> at the University   of Michigan medical  school.</p>
</blockquote>
</body>
</html>

<?
  }
  else{
        $keyword=$_POST['provider'];
        $keyword=ltrim($keyword);
        $keyword=rtrim($keyword);
        searchDataSet($keyword);
  }
?>
