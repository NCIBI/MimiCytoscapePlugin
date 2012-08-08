<?
include('./adodb/adodb.inc.php');
$conn =& ADONewConnection('odbc_mssql');	
print "<h1>Connecting to $conn->databaseType...</h1>";
$dsn = "Driver={SQL Server};Server=dbx.ncibi.org:1434;Database=mimiR2;";	
$conn->Connect($dsn,'userMimiCytoPlugin','GoBlue08!');
#$recordSet = &$conn->Execute('exec mimiCytoPlugin.denormMimiR2Attributes 1, 1436, 1436'); 
$recordSet = $conn->Execute('select top 10 * from moleculegene');
if (!$recordSet) 
        print $conn->ErrorMsg(); 
else 
       while (!$recordSet->EOF) { 
            print $recordSet->fields[0].' '.$recordSet->fields[1].'<BR>'; 
            $recordSet->MoveNext(); 
        }    $recordSet->Close(); # optional 
$conn->Close(); # optional 


?>
