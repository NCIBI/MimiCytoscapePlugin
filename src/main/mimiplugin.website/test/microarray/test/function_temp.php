<?php


//connect to database server
function conn_db($user="bionlp", $pwd="bionlp04", $db="bionlp",$server="DB1"){

   $connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
   mssql_select_db( $db);
}


//extract logged in reviewer ID. this function is called in index.php
function handle_username($name) {
   //$connection = mssql_connect($server, $user,  $pwd) or die("Unable to connect to server");
   //mssql_select_db( $db);
   
   conn_db();
   //if user exist in DragoReviewer table, extract userID
   $query_ReviewerID = "SELECT ReviewerID FROM DragoReviewers WHERE
                                  ReviewerName='$name'";  
   $resultReviewerID= mssql_query($query_ReviewerID) or die("Sorry, query failed."); //call function
   if (!($myrow = mssql_fetch_array($resultReviewerID))){
             
        $insertReviewerName = "INSERT INTO DragoReviewers VALUES
                               ('$name')";
        mssql_query($insertReviewerName);

        //extract ReviewerID
        $query_ReviewerID = "SELECT ReviewerID FROM DragoReviewers WHERE
                             ReviewerName='$name'";
        $resultReviewerID= mssql_query($query_ReviewerID) or die("Sorry, query failed."); //call function
        $myrow = mssql_fetch_array($resultReviewerID);

   }

   //return ReviewerID   
   return($myrow[0]);
}


//display score sentences form with different background colors according to  
//if sentences has been scored by current reviewer (yellow) or by other person(gray)
//or not yet scored (white)

function displayNextBatch($curPos, $ReviewerID)
{
                
                conn_db();   //connect to db server

                // if meet the last sentence, cursor will start from 0 
                $theres=mssql_query("select  count(*) from DragoSents where isprot=1 OR isprot=2");
                $therow = mssql_fetch_array($theres); 
                $totalPortA = $therow[0];
                if($curPos > $totalPortA){
                   $curPos= 0;
                }

                $current_count = 0;
                //declare a cursor order by protein A
                $cursor = mssql_query("DECLARE sent_cursor SCROLL CURSOR FOR SELECT * FROM DragoSents  WHERE isprot=1
                                       OR isprot=2 ORDER BY protA");
                $cursor = mssql_query("OPEN sent_cursor");
                $cur=$cursor;
                $i=0;
                 
                // declare an array for storing all sentences infor of current page
                $thispage = array();

                $retStr = "<table cellspacing=1
                bgcolor=black><th bgcolor=white>Protein 1</th><th bgcolor=white>Protein 2</th><th bgcolor=white halign=center>Interacts?<br /><font size=-1>(checked=YES)</font></th><th bgcolor=white>Sentence</th>\n";
                while ($i<10)
                {
                        $cursor = mssql_query("FETCH ABSOLUTE $curPos FROM sent_cursor");
                          
                        //cursor position plus 1
                        $curPos++;
                        //display counter plus 1 
                        $i++;

                        //get the current sentence 
                        $row = mssql_fetch_array($cursor);

                        //get reviewerID, it is a global var 
                        $reviewerID = $_SESSION['ReviewerID'];
                         
                        //get protein infor from DragoSents
                        $proteinA = $row['protA'];
                        //put sid infor to array, this will hold all the current page sid infor,
                        //unchecked sentences sid  will be extracted from this array 
                        $thispage[$i-1] = $row['sid'];
                                          
                        //check if the sentence has been scored
                        $sid = $row['sid'];
                        $result0 = mssql_query("SELECT top 1 sid, decision FROM DragoSurvey WHERE sid = '$sid' order by datestamp desc");
                        $resRow0 = mssql_fetch_array($result0);
                        $result1 = mssql_query("SELECT top 1 sid, decision FROM DragoSurvey WHERE sid = '$sid' AND ReviewerID='$ReviewerID' order by datestamp desc");
                        $resRow = mssql_fetch_array($result1);
                        if ($resRow0 == ""){ // if sentence is not scored yet, set background color white 
                        
                            $retStr .= "<tr>
                            <td bgcolor=white>".$row['protA']."</td><td bgcolor=white>".$row['protB']."</td><td bgcolor=white align=center>
                            <input name=\"decision[]\" type=\"checkbox\" id=\"decision[]\"
                            value=\"".$row['sid']."\" checked=\"checked\"></td>
                            <td bgcolor=white>".$row['sent']."</td>
                            </tr>\n";
                       }
                       else { //if sentence is scored by others, set bgcolor gary
                             if ($resRow == ""){

                                $decsi = $resRow0['decision'];
                                if(strncasecmp($decsi, "1",1) == 0){
                                   $retStr .= "<tr>
                                   <td bgcolor=white>".$row['protA']."</td><td bgcolor=white>".$row['protB']."</td><td bgcolor=white align=center>
                                   <input name=\"decision[]\" type=\"checkbox\" id=\"decision[]\"
                                   value=\"".$row['sid']."\" checked=\"checked\"></td>
                                   <td bgcolor='dcdcdc'>".$row['sent']."</td>
                                   </tr>\n";
                                }
                                else{
                                $retStr .= "<tr>
                                <td bgcolor=white>".$row['protA']."</td><td bgcolor=white>".$row['protB']."</td><td bgcolor=white align=center>
                                <input name=\"decision[]\" type=\"checkbox\" id=\"decision[]\"
                                value=\"".$row['sid']."\" ></td>
                                <td bgcolor='dcdcdc'>".$row['sent']."</td>
                                </tr>\n";
                               }
                            }
                            else {  //if sentence is scored by current reviewer, set bgcolor yellow
                                  $decsi = $resRow['decision'];
                                  if(strncasecmp($decsi, "1",1) == 0){
                                      $retStr .= "<tr>
                                      <td bgcolor=white>".$row['protA']."</td><td bgcolor=white>".$row['protB']."</td><td bgcolor=white align=center>
                                      <input name=\"decision[]\" type=\"checkbox\" id=\"decision[]\"
                                      value=\"".$row['sid']."\" checked=\"checked\"></td>
                                      <td bgcolor='ffffcc'>".$row['sent']."</td>
                                      </tr>\n";
                                  }
                                  else{
                                       $retStr .= "<tr>
                                       <td bgcolor=white>".$row['protA']."</td><td bgcolor=white>".$row['protB']."</td><td bgcolor=white align=center>
                                       <input name=\"decision[]\" type=\"checkbox\" id=\"decision[]\"
                                       value=\"".$row['sid']."\" ></td>
                                       <td bgcolor='ffffcc'>".$row['sent']."</td>
                                       </tr>\n";
                                 } 
                            }
                       }
                }
                $retStr .= "</table>\n";
                $_SESSION['THISPAGE'] = $thispage ;
                print "$retStr";
                return $curPos;

}



//set first cursor position of distinct protA
function SetProtAPos(){
          
          conn_db();
         


          $cursor = mssql_query("DECLARE sent_cursor_by_protA SCROLL CURSOR FOR SELECT * FROM DragoSents  WHERE isprot=1
                                 OR isprot=2 ORDER BY protA");
          $cursor = mssql_query("OPEN sent_cursor_by_protA");   

          $result = mssql_query("SELECT DISTINCT protA FROM DragoSents WHERE isprot=1 OR isprot=2 ORDER BY protA") or die("Query Failed");
          $protA  = array();
          $protAPos = array();
          $j=0;
          $i=0;
          while( $myrow = mssql_fetch_array($result)){
                 //print "$myrow[0]\n";
                 $notFound=1;
                 
                 while($notFound){
                        $cursor = mssql_query("FETCH ABSOLUTE $i FROM sent_cursor_by_protA");
                        $row = mssql_fetch_array($cursor);
                        if($row['protA'] == $myrow[0]){
                           $protAPos[$j] = $i;
                           $protA[$j] = $myrow[0];
                           $j++;
                           $notFound = 0;   
                        }
                        else{
                           $i++;
                        }
                 }
          }

          mssql_query("CLOSE sent_cursor_by_protA");
          mssql_query("DEALLOCATE sent_cursor_by_protA");
          
          $_SESSION['protAPos'] = $protAPos;
          $_SESSION['protA'] = $protA;  
          
                
} 



function getReviewInfo(){
         $reviewerID = $_SESSION['ReviewerID'];
	 $retStr = "";
         //print "$reviewerID\n";
                        # Count total done by this user
                        $viewInfo = mssql_query("SELECT SentencesReviewed, TotalProteinSentences FROM SurveyProgress WHERE ReviewerID ='$reviewerID' AND
                                                  ProteinA IS NULL" );
                        $result = mssql_fetch_array($viewInfo);
                        $retStr .= "Reviewed so far: ".$result[0]."<br />";
                        # Count total done by all users
                        $viewInfo = mssql_query("SELECT SentencesReviewed, TotalProteinSentences FROM SurveyProgress WHERE ReviewerID IS NULL AND
                                                  ProteinA IS NULL AND ReviewerName IS NULL");
                        $result = mssql_fetch_array($viewInfo);
                        $retStr .= "Reviewed by all users: ".$result[0]."<br />";
	return $retStr;
}




//create a temporary table. this function is called by index.php
function create_table(){
         //conn_db();
        // mssql_query("CREATE TABLE DragoTemp (sid varchar (50))");
            
}




?>
