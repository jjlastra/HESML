#!/usr/bin/perl
use Time::HiRes qw[gettimeofday tv_interval];
use Text::CSV;
use UMLS::Interface;


my $measure = $ARGV[0] or die "Unable to read the measure input [path|upath|wup|cmatch|batet|sanchez|pks|closeness|zhong|lch|cdist|nam|vector|res|lin|faith|random|jcn|lesk|o1vector]\n";

my $vocab   = $ARGV[1] or die "Unable to read the vocabulary input [snomedct_us|msh]\n";


# load the csv file with the list of CUIs codes

my $file = "/tmp/tempFile.csv";
open(my $cuis_csv_codes, '<', $file) or die "Unable to open the file with CUI codes '$file' $!";
die "Unable to create load CUIS codes." if(!$cuis_csv_codes);

# Initialize the measure

my $meas;

#  initialize the ic and path options hash tables

my %icoptions = ();

my $start_run_loading = time();

# initialize the interface

my $umls;

#  Initialize the interface

if($measure=~/res|jcn|lin|faith/) 
{ 
	if($vocab=~/snomedct_us/) 
	{
		$umls = UMLS::Interface->new({
		    #"realtime"      => "1",
		    #"verbose"       => "1",
		    "config"        => "./UMLS_Similarity_Perl/icmeasures.config",
		    #"debugpath"     => "file"
   		});
	}
	else
	{
		$umls = UMLS::Interface->new({
			#"t"      => "1",
		    #"realtime"      => "1",
		    #"verbose"       => "1",
		    "config"        => "./UMLS_Similarity_Perl/icmeasuresMSH.config",
		    #"debugpath"     => "file"
   		});
	}

}
else
{
	if($vocab=~/snomedct_us/) 
	{
		$umls = UMLS::Interface->new({
		    #"realtime"      => "1",
		    #"verbose"       => "1",
		    "config"        => "./UMLS_Similarity_Perl/measure.config",
		    #"debugpath"     => "file"
   		});
	}
	else
	{
		$umls = UMLS::Interface->new({
		    #"realtime"      => "1",
		    #"verbose"       => "1",
		    "config"        => "./UMLS_Similarity_Perl/measureMSH.config",
		    #"debugpath"     => "file"
   		});
	}
}

my %pathoptions = ();

#  load the module implementing the Leacock and
#  Chodorow (1998) measure

if($measure eq "lch")
{
    use UMLS::Similarity::lch;
    $meas = UMLS::Similarity::lch->new($umls);
}

#  loading the module implementing the Wu and
#  Palmer (1994) measure

if($measure eq "wup")
{
    use UMLS::Similarity::wup;
    $meas = UMLS::Similarity::wup->new($umls);
}

#  loading the module implementing the Maedche and
#  Staab (2001) measure

if($measure eq "cmatch")
{
    use UMLS::Similarity::cmatch;
    $meas = UMLS::Similarity::cmatch->new($umls);
}

#  loading the module implementing the Sanchez, et al.
#  (2012) measure

if($measure eq "sanchez")
{
    use UMLS::Similarity::sanchez;
    $meas = UMLS::Similarity::sanchez->new($umls);
}

#  loading module implementing the Batet, et al. (2011)
#  measure

if($measure eq "batet")
{
    use UMLS::Similarity::batet;
    $meas = UMLS::Similarity::batet->new($umls);
}

#  loading the module implementing the Pekar and
#  Staab (2002) measure

if($measure eq "pks")
{
    use UMLS::Similarity::pks;
    $meas = UMLS::Similarity::pks->new($umls);
}

#  loading the module implementing the Zhong
#  et al (2002) measure

if($measure eq "zhong")
{
    use UMLS::Similarity::zhong;
    $meas = UMLS::Similarity::zhong->new($umls, %pathoptions);
}

#  loading the module implementing the simple edge counting
#  measure of semantic relatedness.

if($measure eq "path")
{
    use UMLS::Similarity::path;
    $meas = UMLS::Similarity::path->new($umls);
}

#  loading the module implementing the undirected edge counting
#  measure of semantic relatedness.

if($measure eq "upath")
{
    use UMLS::Similarity::upath;
    $meas = UMLS::Similarity::upath->new($umls, %pathoptions);
}

#  load the module implementing the Rada, et. al.
#  (1989) called the Conceptual Distance measure

if($measure eq "cdist")
{
    use UMLS::Similarity::cdist;
    $meas = UMLS::Similarity::cdist->new($umls, %pathoptions);
}

#  load the module implementing the Nguyen and
#  Al-Mubaid (2006) measure

if($measure eq "nam")
{
    use UMLS::Similarity::nam;
    $meas = UMLS::Similarity::nam->new($umls, %pathoptions);
}

#  load the module implementing the Resnik (1995) measure

if($measure eq "res")
{
    use UMLS::Similarity::res;
    $meas = UMLS::Similarity::res->new($umls, %icoptions);
}

#  load the module implementing the Jiang and Conrath
#  (1997) measure

if($measure eq "jcn")
{
    use UMLS::Similarity::jcn;
    $meas = UMLS::Similarity::jcn->new($umls, %icoptions);
}

#  load the module implementing the Lin (1998) measure
if($measure eq "lin")
{
    use UMLS::Similarity::lin;
    $meas = UMLS::Similarity::lin->new($umls, %icoptions);
}

#  load the module implementing the Perro and Euzenat (2010) measure
if($measure eq "faith")
{
    use UMLS::Similarity::faith;
    $meas = UMLS::Similarity::faith->new($umls, %icoptions);
}

#  load the module implementing the random measure
if($measure eq "random")
{
    use UMLS::Similarity::random;
    $meas = UMLS::Similarity::random->new($umls);
}
die "Unable to create UMLS::Similarity object." if(!$meas);

my $end_run_loading = time();
my $run_time_loading = $end_run_loading - $start_run_loading;
print "Initialized UMLS::Similarity measure in $run_time_loading segs \n";

my $csv = Text::CSV->new({ sep_char => ';' });

my $csv_write = Text::CSV->new({ binary => 1, auto_diag => 1, eol => "\n"})
        or die "Cannot use CSV: " . Text::CSV->error_diag();

# open in append mode
open my $fh, ">>", "/tmp/tempFileOutput.csv" or die "Failed to open file: $!";

# Reading the file

while (my $line = <$cuis_csv_codes>)
{
    chomp $line;

    # Parsing the line
    if ($csv->parse($line))
    {
        my @words = $csv->fields();
        my $cui1 = $words[0];
        my $cui2 = $words[1];

	# print "Calculating the similarity between $cui1 and $cui2 \n";

	my $start_time = Time::HiRes::gettimeofday();

        my $pvalue = $meas->getRelatedness($cui1, $cui2);
	
	my $stop_time = Time::HiRes::gettimeofday();
	
	my $run_time = $stop_time - $start_time;

        print "The similarity between $cui1 and $cui2 is <> $pvalue <>, in $run_time segs. \n";

	$csv_write->print($fh, [ $cui1, $cui2, $pvalue, $run_time ]);

        # print "\n";
    }
}

# close the file

close $fh;
