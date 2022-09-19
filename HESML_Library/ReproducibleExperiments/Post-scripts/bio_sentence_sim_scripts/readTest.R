# We load the input raw results file for Test-based measures

rawdata_BIOSSES_Test <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Test.csv"), dec = ".", sep = ';')
rawdata_MedSTS_Test  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Test.csv"), dec = ".", sep = ';')
rawdata_CTR_Test     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Test.csv"), dec = ".", sep = ';')

rawdata_Test <- list( label = "Test-based", biosses = rawdata_BIOSSES_Test, medsts = rawdata_MedSTS_Test, ctr = rawdata_CTR_Test)
