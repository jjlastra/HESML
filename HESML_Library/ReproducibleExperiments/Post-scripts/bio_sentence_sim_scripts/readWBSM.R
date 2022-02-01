################################
# We load the input raw results file for WBSM-based measures
################################

# Stop-words processing

rawdata_BIOSSES_WBSMStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_WBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_WBSMStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_WBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_WBSMStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_WBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_WBSMStopWords)[colnames(rawdata_BIOSSES_WBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_WBSMStopWords)[colnames(rawdata_BIOSSES_WBSMStopWords) != "Human"])
colnames(rawdata_MedSTS_WBSMStopWords)[colnames(rawdata_MedSTS_WBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_WBSMStopWords)[colnames(rawdata_MedSTS_WBSMStopWords) != "Human"])
colnames(rawdata_CTR_WBSMStopWords)[colnames(rawdata_CTR_WBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_WBSMStopWords)[colnames(rawdata_CTR_WBSMStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_WBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_WBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_WBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_WBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_WBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_WBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_WBSMCF)[colnames(rawdata_BIOSSES_WBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_WBSMCF)[colnames(rawdata_BIOSSES_WBSMCF) != "Human"])
colnames(rawdata_MedSTS_WBSMCF)[colnames(rawdata_MedSTS_WBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_WBSMCF)[colnames(rawdata_MedSTS_WBSMCF) != "Human"])
colnames(rawdata_CTR_WBSMCF)[colnames(rawdata_CTR_WBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_WBSMCF)[colnames(rawdata_CTR_WBSMCF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_WBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_WBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_WBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_WBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_WBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_WBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_WBSMTOK)[colnames(rawdata_BIOSSES_WBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_WBSMTOK)[colnames(rawdata_BIOSSES_WBSMTOK) != "Human"])
colnames(rawdata_MedSTS_WBSMTOK)[colnames(rawdata_MedSTS_WBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_WBSMTOK)[colnames(rawdata_MedSTS_WBSMTOK) != "Human"])
colnames(rawdata_CTR_WBSMTOK)[colnames(rawdata_CTR_WBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_WBSMTOK)[colnames(rawdata_CTR_WBSMTOK) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_WBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_WBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_WBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_WBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')
rawdata_CTR_WBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_WBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_WBSMLC)[colnames(rawdata_BIOSSES_WBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_WBSMLC)[colnames(rawdata_BIOSSES_WBSMLC) != "Human"])
colnames(rawdata_MedSTS_WBSMLC)[colnames(rawdata_MedSTS_WBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_WBSMLC)[colnames(rawdata_MedSTS_WBSMLC) != "Human"])
colnames(rawdata_CTR_WBSMLC)[colnames(rawdata_CTR_WBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_WBSMLC)[colnames(rawdata_CTR_WBSMLC) != "Human"])

rawdata_BIOSSES_WBSMBestMeasure <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_WBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')
rawdata_MedSTS_WBSMBestMeasure  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_WBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')
rawdata_CTR_WBSMBestMeasure     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_WBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')


# We merge the tables into one table for each dataset

WBSM_BIOSSES <- cbind(rawdata_BIOSSES_WBSMStopWords,rawdata_BIOSSES_WBSMCF,rawdata_BIOSSES_WBSMTOK,rawdata_BIOSSES_WBSMLC, rawdata_BIOSSES_WBSMBestMeasure)
WBSM_MedSTS <- cbind(rawdata_MedSTS_WBSMStopWords,rawdata_MedSTS_WBSMCF,rawdata_MedSTS_WBSMTOK,rawdata_MedSTS_WBSMLC, rawdata_MedSTS_WBSMBestMeasure)
WBSM_CTR <- cbind(rawdata_CTR_WBSMStopWords,rawdata_CTR_WBSMCF,rawdata_CTR_WBSMTOK,rawdata_CTR_WBSMLC, rawdata_CTR_WBSMBestMeasure)

rawdata_WBSM <- list( label = "WBSM", biosses = WBSM_BIOSSES, medsts = WBSM_MedSTS, ctr = WBSM_CTR)