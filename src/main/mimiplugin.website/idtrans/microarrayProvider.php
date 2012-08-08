<?
   session_start();
   include('functions.php');
   if (isset ($_GET['textfield2'])){
      $_SESSION['TEXT'] = $_GET['textfield2'];
   }   
   if (isset($_GET['curPos'])){
       $curPos =$_GET['curPos'];
   }
   else {
       $curPos = $_SESSION['CURSORPOSITION'];
   }
   if (isset($_GET['textField'])){
      $page=$_GET['textField'];
      $curPos = 20*($page-1);
   }
   $_SESSION['CURSORPOSITION'] = $curPos;
   $seriesArry = array(); 
   $seriesArry = getPages();
   $pages = $seriesArry[0];
   $totalSeries =  $seriesArry[1];
   $_SESSION['PAGE'] = $pages;
   $_SESSION['TOTALSERIES']=$totalSeries;
?>
<html>
<head>
<script LANGUAGE="JavaScript" >
function send2Main(seriesName,seriesID){
     var sampleIds="";
     opener.document.form1.provider.value=seriesName;
     obj=eval("document.formInFunc"+seriesID+".checkbox"+seriesID);
     for (i=0;i<obj.length;i++){
          if (obj[i].checked ==true){
              sampleIds +="'"+obj[i].value+"',";
          } 
     }
     opener.document.form1.checkedSample.value=sampleIds;
     self.close();
}

function searchText(){
     text=document.getElementById('textfield2').value;
     document.getElementById('formInMicroarrayProvider1').action="microarrayProvider.php?textfield2="+text;
}

function showSample(id){
   var  obj= eval("sample"+id);
    if (obj.style.display == ""){
        obj.style.display="none";
    }
    else {
        obj.style.display="";
    }

}

function selectAll(obj, id){
   var checkedOrNo = obj.checked;
   var checkbox = "checkbox"+id;
   var col = document.getElementsByName(checkbox);
   for (var i=0;i<col.length;i++) {
       col[i].checked= checkedOrNo;
   }
}


</script>
</head>
<body>
<h2> Experiment Name list</h2>
<? if(isset($_GET['textfield2'])){dipSearchResult($_GET['textfield2']); }
 else { ?>

<?
   $curPos=dispProvider($_SESSION['CURSORPOSITION'],$_SESSION['PAGE']);    
   $_SESSION['CURSORPOSITION'] = $curPos;
?>

<center>
<form method="get" name="formInMicroarrayProvider2"  action="microarrayProvider.php">
<table>
<tr>   
<? if ($_SESSION['CURSORPOSITION'] <= 20){ ?>
<td>First Page </td>
<td>Previous Page  </td>
<td><INPUT type="text" size =4 name ="textField" id ="textField" value="<? print (($_SESSION['CURSORPOSITION']-20)/20 +1); ?>"></td>
<td><a href="microarrayProvider.php?curPos=<? print ($_SESSION['CURSORPOSITION']); ?>">Next Page
</a></td>
<td><a href ="microarrayProvider.php?curPos=<? print (($_SESSION['PAGE']-1)*20);?>">Last Page </a></td>
<? } else if ($_SESSION['CURSORPOSITION']> $_SESSION['TOTALSERIES'] ) {?>
<td><a href="microarrayProvider.php?curPos=0">First Page </a></td>
<td><a href="microarrayProvider.php?curPos=<? print ($_SESSION['CURSORPOSITION']-40); ?>">Previous Page
</a> </td>
<td><INPUT type="text" size =4 name ="textField" value="<? print (($_SESSION['CURSORPOSITION']-20)/20 +1); ?>" ></td>
<td>Next Page</td>
<td>Last Page</td>
<? }else{ ?>
<td><a href="microarrayProvider.php?curPos=0">First Page </a></td>
<td><a href="microarrayProvider.php?curPos=<? print ($_SESSION['CURSORPOSITION']-40);  ?>">Previous Page
</a> </td>
<td><INPUT type="text" size =4 name ="textField" value="<? print (($_SESSION['CURSORPOSITION']-20)/20 +1); ?>"></td>
<td><a href="microarrayProvider.php?curPos=<? print ($_SESSION['CURSORPOSITION']); ?>">Next Page
</a></td>
<td><a href ="microarrayProvider.php?curPos=<? print (($_SESSION['PAGE']-1)*20);?>">Last Page </a></td>
<? } ?>
</tr></table></form>
<? } ?>
<form name ="formInMicroarrayProvider1"  id ="formInMicroarrayProvider1"  method ="POST" >
<table>
<INPUT type ="text" name ="textfield2" id="textfield2" ><INPUT type="submit" name ="submit" value="Search" onclick="searchText();">
</table>
</form>
</center>
