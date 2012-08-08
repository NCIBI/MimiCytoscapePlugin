<?
 //$url="http://visant.bu.edu:8080/vserver/DAI?command=time_proxy&target=http://www.genome.jp/kegg/KGML/KGML_v0.6.1/hsa/hsa00020.xml";
       // $ret=exec($url);
$content = '';
//$url="http://www.genome.jp/kegg/KGML/KGML_v0.6.1/hsa/hsa00020.xml";
$url="http://www.yahoo.com";
        if ($fp = fopen($url, 'r')) {
        print "enter";
        // keep reading until there's nothing left
        while ($line = fread($fp, 1024)) {
                $content .= $line;
		print "[$content]";
                }
        fclose($fp);
        }

        print $content;

?>
