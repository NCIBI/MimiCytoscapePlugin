<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Microarray Database Query</title>
<style type="text/css">
<!--
.style1 {font-size: 36px}
body,td,th {
	color: #330066;
}
.style3 {color: #000000}
.style4 {color: #330066; }
.style8 {font-size: medium; font-weight: bold; }
-->
</style>
</head>

<body link='blue' VLINK='blue' ALINK='blue'>
<div align="center" class="style1">
  <p class="style3">&nbsp;</p>
  <p class="style4">Microarray Database Query</p>
  <hr width="622">
  
</div>
<div align="center">
<form  method="post" name="form2" action="./search.php">
  <table width="622" border="0" bordercolor="#FFFFFF">
    <tr bordercolor="#FFFFFF">
      <td colspan="2" bordercolor="#FFFFFF" ><div align="left" class="style1"><span class="style8">Search DataSets:</span></div></td>
    </tr>
     <tr bordercolor="#FFFFFF">
      <td width="107"  align="right" bordercolor="#FFFFFF"><span class="style3">DataSets </span> </td>
      <td width="558" bordercolor="#FFFFFF"><div align="left">
        <input type="text" name="provider" /> 
        <input type="submit" name="firstquery" value="Search" >
      </div></td>
    </tr>
    <tr><td colspan="2"><hr width="622"></td></tr> 
    <tr bordercolor="#FFFFFF">
       <td  colspan="2"  align="left" bordercolor="#FFFFFF"><p class="style8"> Browse DataSets:</p></td>
       <td bordercolor="#FFFFFF">&nbsp;</td>
     </tr>
     <tr bordercolor="#FFFFFF">
       <td  colspan="2"  align="left" bordercolor="#FFFFFF"><a href="displayAllDataset.php">All DataSets</a></td>
     </tr>
     <tr><td colspan="2"><hr width="622"></td></tr>
     <tr bordercolor="#FFFFFF">
       <td  colspan="2"  align="left" bordercolor="#FFFFFF" class="style8">Data Statistics: </td>
       <td bordercolor="#FFFFFF">&nbsp;</td>
     </tr>
     <tr bordercolor="#FFFFFF">
       <td  colspan="2"  align="left" bordercolor="#FFFFFF" >
         <p>Platforms: 86 &nbsp; Experiments:3000 Samples:26000 </p>
       </td>
       <td bordercolor="#FFFFFF">&nbsp;</td>
     </tr>
     <tr><td colspan="2"><hr width="622"></td></tr>
</table>
</form>
</div>	

</body>

</html>
