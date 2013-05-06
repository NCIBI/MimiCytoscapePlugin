<?
include("function.php");
$geneList=(!empty($_POST['GENEID']))? $_POST['GENEID']:$_GET['GENEID'];
$db="humdb";
$ret="";
conn_db_db3();
mssql_select_db($db);
$query="exec denorm.gene2Cmpd '$geneList'";
$results=mssql_query($query) or die("$query failed\n");
$len=mssql_num_fields($results);
while ($myrow = mssql_fetch_array($results)){
                for ($i=0;$i<$len-1;$i++){
                        if ($myrow[$i])
                                $ret.="$myrow[$i]/////";
                        else $ret.=" /////";
                }
                if ($myrow[$len-1])
                        $ret.=$myrow[$len-1]."\n";
                else $ret.=" \n";
}

mssql_close();
print $ret;
?>
