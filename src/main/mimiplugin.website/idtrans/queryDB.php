<?php
   include('./functions.php');

   //generate array of species and platform integer=>species, integer=>platform
   $arr=array(1=>'Homo sapiens',2=>'Mus musculus',3=>'Rattus norvegicus', 101=>'HG-U95Av2',102=>'HG-U133A',201=>'MG-U74Av2',202=>'MOE430A',203=>'MG-U74A', 301=>'RAE230A', 302=>'RAE230B');

   //get species and platform from html form
   $species=$arr[$_POST['selectSpecies']];
   $platform =$arr[ getPlatform($_POST['selectSpecies'])]; 
   //get selected sample ids 
   $sampleIDs="";
   if (isset($_POST['checkedSample'])){
        $sampleIDs= $_POST['checkedSample'];   
        $a= substr($sampleIDs, 1,1);
        if ((strlen($sampleIDs)>2)&&(substr($sampleIDs, 1,1) != 'o')){
            $sampleIDs=substr($sampleIDs,0,strlen($sampleIDs)-1);
        }   
        else {$sampleIDs="";}     
   }   

   //if provider is set
   if (isset($_POST['provider'])){
      $provider = $_POST['provider'];
      $provider =ltrim($provider);
      $provider=rtrim($provider);
      //if Genes are set
      if (isset($_POST['textarea'])){
          $genes= $_POST['textarea'];
          displayMicroarrayWithProvider($provider, $species, $platform, $genes,$sampleIDs);
      }
      else {
      }      
   }
   else if(isset($_POST['dataset'])){
   }
   else{ 
         print "Please go to <a href='http://www.bioinformatics.med.umich.edu/app/nlp/microarray/'>first page</a> to begin your query.";
      }
?>
