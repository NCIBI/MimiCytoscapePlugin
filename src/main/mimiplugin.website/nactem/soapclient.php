<?php
print "using nactem...\n";
// Create an object to access the Acromine Web Service.
$client = new SoapClient("http://www.nactem.ac.uk/software/acromine/acromine.wsdl");

// Retrieve definition IDs for the short form "HMM".
$defids = $client->retrieve("HMM", "");

// For each definition ID...
foreach ($defids as $defid) {
	// Obtain the statistical information of the definition.
	$stat = $client->get_stat($defid);
	foreach ($stat as $key => $value) {
		print "$key: $value\n";
	}

	// Obtain the source information of the definition.
	$var = $client->get_sources($defid);
	print $var;

	print "\n";
}

?>

