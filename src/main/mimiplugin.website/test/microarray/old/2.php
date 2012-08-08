
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/JavaScript">
function displayInit(){
	document.form2.submit();
  return;
}

</script>
</head>

<body onLoad="displayInit()">
<form  method="post" name="form2" action="http://enigma.eecs.umich.edu/cgi-bin/saga_direct_new.cgi"> 
<!-- <form  method="post" name="form2" action="./sagatest1.php"> -->
        <input type="text" name="database" value="hsa_2007_11_1" /> 
	<input type="text" name="percent" value="20.0"  />
	<input type="text" name="query" value="csf1rHomosapiensproteinAllDataSou$3$INPPL1 1 K01106$CSF1 1 K05453$CSF1R 1 K05090$2$2 0 1$2 1 1$"/>
        <input type="submit" name="firstquery" value="Search" >
<title>Microarray Database Query</title>
</form>

</body>

</html>
