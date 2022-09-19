# We load the input raw results file for string-based measures

rawdata_BIOSSES_BESTCOMBS <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_BESTCOMBS.csv"), dec = ".", sep = ';')
rawdata_MedSTS_BESTCOMBS  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_BESTCOMBS.csv"), dec = ".", sep = ';')
rawdata_CTR_BESTCOMBS     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_BESTCOMBS.csv"), dec = ".", sep = ';')

rawdata_BESTCOMBS <- list( label = "best combinations", biosses = rawdata_BIOSSES_BESTCOMBS, medsts = rawdata_MedSTS_BESTCOMBS, ctr = rawdata_CTR_BESTCOMBS)
