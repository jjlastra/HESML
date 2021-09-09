################################
# We load the input raw results file for COM-based measures
################################

rawdata_BIOSSES_COM <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_COM.csv"), dec = ".", sep = ';')
rawdata_MedSTS_COM  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_COM.csv"), dec = ".", sep = ';')
rawdata_CTR_COM     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_COM.csv"), dec = ".", sep = ';')


rawdata_COM <- list( label = "COM", biosses = rawdata_BIOSSES_COM, medsts = rawdata_MedSTS_COM, ctr = rawdata_CTR_COM)