# We load the input raw results file for string-based measures

rawdata_BIOSSES_string <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Stringbased_ALLPreprocessingCombs.csv"), dec = ".", sep = ';')
rawdata_MedSTS_string  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Stringbased_ALLPreprocessingCombs.csv"), dec = ".", sep = ';')
rawdata_CTR_string     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Stringbased_ALLPreprocessingCombs.csv"), dec = ".", sep = ';')

rawdata_string <- list( label = "String-based", biosses = rawdata_BIOSSES_string, medsts = rawdata_MedSTS_string, ctr = rawdata_CTR_string)
