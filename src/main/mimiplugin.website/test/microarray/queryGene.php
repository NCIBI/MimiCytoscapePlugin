<?
   include('functions.php');
   $sampleIds="";
   $genes="";

   $species=$_POST['species'];
   $platform=$_POST['platformName'];
   $sid=$_POST['seriesID'];
   $seriesName =$_POST['seriesName']; 
   $checkAllField= "selectAllsamples".$sid;
   $checkSamplesField="selectSamples".$sid;
   //no samples checked or all samples checked are treated as same,
   //if some samples checked
   if (!isset($_POST[$checkAllField])&& isset($_POST[$checkSamplesField])){
       $checkedSamples = $_POST[$checkSamplesField];
       foreach($checkedSamples as $sample){
               $sampleIds.="'".$sample."',";
       } 
       $sampleIds=substr($sampleIds,0,-1);
   }
   //get  genes from text area or from uploaded local file
   $uploadfieldid="uploadFile".$sid;
   
   if (isset($_POST['genetextarea']) && !ctype_space($_POST['genetextarea'])) {
       $genes=trim($_POST['genetextarea']);
   }
   else {
            $target_path = "./upload/";
            $uploadfile = $target_path . basename( $_FILES[$uploadfieldid]['name']); 
            if (!move_uploaded_file($_FILES[$uploadfieldid]['tmp_name'], $uploadfile)){
                 print"<h3>upload file failed....</h3>";
                 exit;
            }
            else {
                   $GENEFILE = fopen($uploadfile, 'r');
                   $genes = fread($GENEFILE, filesize($uploadfile));
                   fclose($GENEFILE);
            }
   }
  // echo $genes;
   showMicrProfileWithGenes($species,$platform,$genes,$sampleIds,$seriesName, $sid); 
   
?>
