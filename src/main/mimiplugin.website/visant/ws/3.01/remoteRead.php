<?
ini_set("memory_limit","1024M");
$command=(!empty($_POST['command']))? $_POST['command'] : $_GET['command'];
$targeturl=(!empty($_POST['target']))? $_POST['target'] : $_GET['target'];
//print "target url is $targeturl";
if ($fp = fopen($targeturl, 'r')) {
   $content = '';
   // keep reading until there's nothing left 
   while ($line = fread($fp, 1024)) {
      $content .= $line;
   }
   print $content;
}	

?>
