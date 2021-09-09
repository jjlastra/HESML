################################
# We load the input raw results file for COM-based measures
################################

rawdata_BIOSSES_COMMixed <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_COMMixed.csv"), dec = ".", sep = ';')
rawdata_MedSTS_COMMixed  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_COMMixed.csv"), dec = ".", sep = ';')
rawdata_CTR_COMMixed     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_COMMixed.csv"), dec = ".", sep = ';')


rawdata_COMMixed <- list( label = "COMMixed", biosses = rawdata_BIOSSES_COMMixed, medsts = rawdata_MedSTS_COMMixed, ctr = rawdata_CTR_COMMixed)