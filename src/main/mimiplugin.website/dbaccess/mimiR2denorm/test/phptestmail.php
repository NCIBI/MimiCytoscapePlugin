<?php
$to      = "jinggao@umich.edu";
$subject = "the subject";
$message = "http://mimiplugin.ncibi.org/hello.php";
$headers = "From: webmaster@example.com";

mail($to, $subject, $message, $headers);
?> 

