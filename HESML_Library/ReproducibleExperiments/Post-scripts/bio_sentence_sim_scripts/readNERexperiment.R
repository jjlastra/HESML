# We load the input raw results file for string-based measures

rawdata_BIOSSES_NERexperiment <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_NERexperiment.csv"), dec = ".", sep = ';')
rawdata_MedSTS_NERexperiment  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_NERexperiment.csv"), dec = ".", sep = ';')
rawdata_CTR_NERexperiment     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_NERexperiment.csv"), dec = ".", sep = ';')

rawdata_NERexperiment <- list( label = "NER experiments", biosses = rawdata_BIOSSES_NERexperiment, medsts = rawdata_MedSTS_NERexperiment, ctr = rawdata_CTR_NERexperiment)
