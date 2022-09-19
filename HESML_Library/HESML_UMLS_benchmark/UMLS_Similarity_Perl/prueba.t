use lib '/home/alicia/perl5/lib/perl5/5.26.1/x86_64-linux-gnu-thread-multi';
use lib '/home/alicia/perl5/lib/perl5/5.26.1';
use lib '/home/alicia/perl5/lib/perl5/x86_64-linux-gnu-thread-multi';
use lib '/home/alicia/perl5/lib/perl5';
use lib '/etc/perl';
use lib '/usr/local/lib/x86_64-linux-gnu/perl/5.26.1';
use lib '/usr/local/share/perl/5.26.1';
use lib '/usr/lib/x86_64-linux-gnu/perl5/5.26';
use lib '/usr/share/perl';
use lib '/usr/lib/x86_64-linux-gnu/perl/5.26';
use lib '/usr/share/perl/5.26o';
use lib '/home/alicia/perl5/lib/perl5/5.26.0';
use lib '/home/alicia/perl5/lib/perl5/5.26.0/x86_64-linux-gnu-thread-multi';
use lib '/usr/local/lib/site_perl';
use lib '/usr/lib/x86_64-linux-gnu/perl-base';
use warnings FATAL => 'all';
use strict;

use UMLS::Interface;
use UMLS::Similarity::lch;
use UMLS::Similarity::path;

#my %option_hash = ();
#  check the realtime option
$option_hash{"forcerun"} = 1;
# $option_hash{"config"} = "./measures.config";

my $umls = UMLS::Interface->new(\%option_hash);
die "Unable to create UMLS::Interface object.\n" if(!$umls);

#my $lch = UMLS::Similarity::lch->new($umls);
#die "Unable to create measure object.\n" if(!$lch);

my $path = UMLS::Similarity::path->new($umls);
die "Unable to create measure object.\n" if(!$path);


for (my $i = 0; $i < 2; $i++) {

    my $cuis = <STDIN>;

    chomp $cuis;

    my $cui1 = (split '-', $cuis)[0];
    #print "First CUI is '$cui1'\n";

    my $cui2 = (split '-', $cuis)[1];
    #print "Second CUI is '$cui2'\n";

    #my $term1    = "head";
    #my $tList1   = $umls->getConceptList($term1);
    #my $cui1     = pop @{$tList1};

    #my $sabs1 = $umls->getSab($cui1);
    #print "The sources containing $term1 ($cui1) are: @{$sabs1}\n\n";

    #my $term2    = "nose";
    #my $tList2   = $umls->getConceptList($term2);
    #my $cui2     = pop @{$tList2};

    #my $sabs2 = $umls->getSab($cui2);
    #print "The sources containing $term1 ($cui2) are: @{$sabs2}\n\n";


    #my $lvalue = $lch->getRelatedness($cui1, $cui2);

    my $pvalue = $path->getRelatedness($cui1, $cui2);

    #print "The lch similarity between $cui1 ($term1) and $cui2 ($term2) is $lvalue\n";

    print "The path similarity between $cui1 and $cui2 is <> $pvalue\n";
    #print $pvalue;
}