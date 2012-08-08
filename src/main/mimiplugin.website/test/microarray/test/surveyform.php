<?

        include('./functions.php');
	session_start();
       
        if (!isset($_SESSION['ReviewerID'])){
            print "PLEASE &nbsp; &nbsp;  <a href='http://www.bioinformatics.med.umich.edu/app/nlp/sents/'>  LOG IN </a> &nbsp; &nbsp; NOW\n";
            exit; 
        }
        
 
        $reviewerID = $_SESSION['ReviewerID'];
        $reviewerName = $_SESSION['ReviewerName'];
	$curPos = $_SESSION['CURSORPOSITION'];
	if( isset($_GET['curPos']) )
	{
		$curPos = $_GET['curPos'];
	}
	else
	{
		$curPos = $_SESSION['CURSORPOSITION'];
	}

        $_SESSION['CURSORPOSITION'] = $curPos;

       // $c =  $_SESSION['CURSORPOSITION'];
       // print "[current cursor position is  $c ]\n";

?>


<html>
<head>

     <script language='javascript'>
      function disProtein(cursorpos){
               location.href = "./surveyform.php?curPos=" +cursorpos;
      }
     </script>
</head>

<body>
<p><a href="./help.html" target="_blank">Instructions...</a></p>
<h2><? print "<h2>Currently logged in as: ".$_SESSION['ReviewerName']." (id: ".$_SESSION['ReviewerID'].")</h2>"; ?>
<p><font size="3px">color key: [white=not yet done; yellow=voted by me; grey=already done by others]</font> </p>
<form target="_self" action="./surveyform.php" method="POST">
<?
         //store the vote result to database table
         if( isset( $_POST['decision'] ) )
         {
               // print"enter\n";
                conn_db();
                $decision = $_POST['decision'];
                
                $thispage = $_SESSION['THISPAGE'];
                //print "$thispage[8]\n";

                //store checked result to table
	        foreach( $decision as $dec ) {
                      //print "$dec\n";
                      
                            mssql_query("INSERT INTO DragoSurvey (ReviewerID, sid,decision) VALUES ('$reviewerID', '$dec', '1' )");
	         }

                 //store unchecked result to table
                 $count = count($thispage);
                 //$find = 0;
                 for ($j=0; $j<$count; $j++) {
                      $find = 0;
                      foreach($decision as $Dec){
                              if($thispage[$j]== $Dec){
                                 $find = 1;
                                 break;
                              }
                      }

                      if($find == 0){
                         $sid = $thispage[$j];
                         $thisresult = mssql_query("SELECT sid FROM DragoSurvey WHERE sid='$sid'");
                         $thisrow =  mssql_fetch_array($thisresult);
                              mssql_query("INSERT INTO DragoSurvey (ReviewerID, sid, decision) VALUES ($reviewerID, '$sid', '0')");
                      }

                 }
         }
         else {
               if( isset( $_POST['Submit']) ){
                  conn_db();
                  //print"enter now\n";
                  $thispage = $_SESSION['THISPAGE'];
                  $count = count($thispage);
                  for ($j=0; $j<$count; $j++) {
                         $sid = $thispage[$j];
                         $thisresult = mssql_query("SELECT sid FROM DragoSurvey WHERE sid='$sid'");
                         $thisrow =  mssql_fetch_array($thisresult);
                              mssql_query("INSERT INTO DragoSurvey (ReviewerID, sid, decision) VALUES ($reviewerID, '$sid', '0')");

                  }
               }
         }
        
        //print"dec is \n";
        //display score form and sore the form to a temprary table
	$curPos = DisplayNextBatch($curPos,$_SESSION['ReviewerID']);

        //keep the most recently cursor position
        $_SESSION['CURSORPOSITION'] = $curPos;

?>



<center>
<table>
<tr>
<td><a href="./surveyform.php?curPos=<? print ($_SESSION['CURSORPOSITION']-20); print "\""; ?>>&lt;&lt;&lt;Get Previous 10</a></td>
<td><a href="./surveyform.php?curPos=<? print ($_SESSION['CURSORPOSITION']); print "\""; ?>>Get Next 10&gt;&gt;&gt;</a></td></tr></table>

<br> Select Your Favorite Protein &nbsp;
<select name="protein" onChange="disProtein(protein.options[protein.selectedIndex].value)" >//041705
     <option  value ="0"> NONE
     <?php

                 $protAPos = $_SESSION['protAPos'];
                 $protA = $_SESSION['protA'];
                 for($i=0; $i<count($protA); $i++){
                     $pos = $protAPos[$i];
                     $prot = $protA[$i];
                     print"<option value ='$pos'>";
		     $result = mssql_query("SELECT count(*) FROM DragoSents WHERE (protA='$prot') AND (isprot =1 OR isprot=2)");
		     $rAry = mssql_fetch_array($result);
		     print "$prot (".$rAry[0]." sentences)</option>\n";
                 }
                
 
                
      

      ?>
     </select> 



     
<br /><br />
		<input type="reset" name="Reset" value="Reset">
		<input type="submit" name="Submit" value="Submit Values">

<br><br>
<?php print "<font color=#0000ff siz=10pt>".getReviewInfo()."</font>"; ?>
<br><br>
<a href="./logout.php">Log Out</a>
</form>

</body>
</html>
