# We load the input raw results file for string-based measures

rawdata_BIOSSES_UBSM_AncSPLWeightedJiangConrath <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_AncSPLWeightedJiangConrath.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSM_AncSPLWeightedJiangConrath  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_AncSPLWeightedJiangConrath.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSM_AncSPLWeightedJiangConrath     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_AncSPLWeightedJiangConrath.csv"), dec = ".", sep = ';')


rawdata_BIOSSES_UBSM_Rada <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_AncSPLRada.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSM_Rada  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_AncSPLRada.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSM_Rada     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_AncSPLRada.csv"), dec = ".", sep = ';')


# We merge the tables into one table for each dataset

UBSM_BIOSSES <- cbind(rawdata_BIOSSES_UBSM_AncSPLWeightedJiangConrath, rawdata_BIOSSES_UBSM_Rada)
UBSM_MedSTS <- cbind(rawdata_MedSTS_UBSM_AncSPLWeightedJiangConrath, rawdata_MedSTS_UBSM_Rada)
UBSM_CTR <- cbind(rawdata_CTR_UBSM_AncSPLWeightedJiangConrath, rawdata_CTR_UBSM_Rada)

rawdata_UBSMTEST <- list( label = "UBSM_test", biosses = UBSM_BIOSSES, medsts = UBSM_MedSTS, ctr = UBSM_CTR)
