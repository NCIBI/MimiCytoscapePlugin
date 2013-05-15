<?php

  function checkdb($port, $label, $dbname, $sql) {
    $ok = TRUE;
    $user = "username";
    $pw = "pw";
    $server="dbx.ncibi.org:$port";

    print("test for $label <br /> \n");

    $connection = 
      mssql_connect($server, $user,  $pw) or ($ok=FALSE);
    if (!$ok) { 
      print ("Unable to connect to server for $port <br /> \n");
    }
    else {
      $ok = mssql_select_db($dbname);
      if (!$ok) {
        print("for $port ($label) could not connect to db $db<br /> \n"); 
      }
      $result = mssql_query($sql);
      if ($result) {
        $row = mssql_fetch_array($result);
        if ($row) {
          $value = $row[0];
          if ($value == $label) {
            print("sucess for specific query for $label <br /> \n");
          } else {
            print("sucess on generic query ");
            print("(e.g. select count(*) from X)  <br /> \n");
          }
       } else {
          print("query empty for $port ($label) <br /> \n");
          $ok = FALSE;
        }
      } else {
        print("query not sucessful for $port ($label) <br /> \n");
        $ok = FALSE;
      }
      mssql_free_result($result);
      mssql_close($connection);
      if (!$ok) {
        print("<font color='red'>some problem</font> ");
	print("with connection for $port ($label)<br /> \n");
      } else {
        print ("connection for $port ($label) is <b>ok</b> <br /> \n");
      }
      print ("<br />\n");
    }
  }

  print("start <br /> <br />\n");

  checkdb(1433,"pub","mimiR2","select * from TargetType ");
  checkdb(1436,"db4","serverapps","select count(*) from Task");
  checkdb(1435,"db3","_weymouth","select * from Version;");
  checkdb(1434,"dbx","bionlp","select * from README");

  print("done. \n");
?>

