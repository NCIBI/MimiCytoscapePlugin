#!/usr/bin/perl -w
use SOAP::Lite;
open (OUTFILE, ">queryResult.txt") || die "Could not open output file $!";
my $client = SOAP::Lite->new();
$client->proxy('http://www.bioinformatics.med.umich.edu/app/nlp/soap/dbquery.php');

my $query ="select entrezGeneid from EnsemblEntrez where ensemblgeneid IN (select ensemblgeneid from PlatformGeneProbe where probename='100001_at')";
my $som = $client->dbquery("ncibi-dbx", "microarray","ncibipubmed", "ncibi2006", $query) || die "Query failed $!"; # parameters (server, database, user, password, query)
print OUTFILE $som->result();
