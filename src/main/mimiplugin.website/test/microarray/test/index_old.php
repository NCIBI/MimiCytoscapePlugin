<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<script type="text/JavaScript">
<!--
function Change_Platform (e){ 
  if (!e) var e = window.event;
    
    switch (parseInt(e.value)) {
    case 1:
        document.getElementById('platformHuman').style.display="";
        document.getElementById('platformMouse').style.display="none";
        document.getElementById('platformRat').style.display="none";
        


    
        break;
    case 2:
        document.getElementById('platformHuman').style.display="none";
        document.getElementById('platformMouse').style.display="";
        document.getElementById('platformRat').style.display="none";


     
     
        break;
    case 3:
        document.getElementById('platformHuman').style.display="none";
        document.getElementById('platformMouse').style.display="none";
        document.getElementById('platformRat').style.display="";


        break;
    }
    
    return;
  
}
function formAction () {
   document.getElementById('form1').action="queryDB.php";
   return true;
}
//-->
</script>
</head>

<body>
<div align="center" class="style1">
  <p class="style3">&nbsp;</p>
  <p class="style4">Microarray Database Query</p>
  <hr width="600" />
  </div>


<div align="center">
<form action="" method="post" name="form1">
  <table width="622" border="0" bordercolor="#FFFFFF">
    <tr bordercolor="#FFFFFF">
      <td colspan="2" bordercolor="#FFFFFF" ><div align="left" class="style1"><span class="style8">Please Specify a Provider or  a dataset:</span></div></td>
    </tr>
    <tr bordercolor="#FFFFFF">
      <td width="107"  align="right" bordercolor="#FFFFFF"><span class="style3">Provider Name</span> </td>
      <td width="558" bordercolor="#FFFFFF"><div align="left">
        <input type="text" name="provider" />
        <strong> OR</strong> <span class="style3">Data Set Name</span> 
        <input type="text" name="textfield" />
      </div></td>
    </tr>
    <tr bordercolor="#FFFFFF">
      <td colspan="2" align="left">&nbsp;</td>
    </tr>
    <tr bordercolor="#FFFFFF">
      <td colspan="2" align="left"><div align="left" class="style8">Please Select Species, Platform, Tissues and Genes: </div>
      <div align="center"></div></td>
    </tr>
    <tr>
      <td bordercolor="#FFFFFF"><div align="right" class="style3">
        <div align="left">Species</div>
      </div></td>
      <td bordercolor="#FFFFFF"><div align="left"><select name="selectSpecies" id="selectSpecies" onchange="Change_Platform(this);">
        <option value=1 selected >Human</option>
        <option value=2 >Mouse</option>
        <option value=3>Rat</option>
		<option value=4>Yeast</option>
		<option value=5>Fruitfly</option>
		<option value=6>E Coli</option>		
      </select></div></td>
    </tr>
    <tr>
      <td bordercolor="#FFFFFF"><div align="right" class="style3">
        <div align="left">Platforms</div>
      </div></td>
      <td bordercolor="#FFFFFF"><div align="left">
	  <select name="platformHuman" id="platformHuman" >
        <option value=101 selected >HGU95AV2</option>
        <option value=102>HGU133A</option>
      </select>
	  <select name="platformMouse"  id="platformMouse" style="display:none" >
	  <option value=201 selected>MG-U74Av2</option>
	  <option value=202>MOE430A</option>
	  </select>
	  <select name="platformRat"  id="platformRat" style="display:none" >
	  <option value=301 selected>RAE230A</option>
	  <option value=302>RAE230B</option>
	  </select>
</div>     </td>
    </tr>
    
    <tr>
      <td bordercolor="#FFFFFF"><div align="right" class="style3">
        <div align="left">Genes</div>
      </div></td>
      <td bordercolor="#FFFFFF"><div align="left"><textarea name="textarea" cols="40" rows="10"></textarea></div></td>
    </tr>
    <tr>
      <td bordercolor="#FFFFFF">&nbsp;</td>
      <td bordercolor="#FFFFFF">&nbsp;</td>
    </tr>
  </table>
  <p>
    <input type=submit value="Query"  onclick=formAction(); >
    <input type="reset" value="Reset" onclick=resetbutt(); >
  </p>
  </form>
</div>
<p>&nbsp;</p>
</body>

</html>
