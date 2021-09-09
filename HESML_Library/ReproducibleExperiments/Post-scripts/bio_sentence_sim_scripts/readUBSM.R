################################
# We load the input raw results file for UBSM-based measures
################################

# Stop-words processing

rawdata_BIOSSES_UBSMStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMStopWords)[colnames(rawdata_BIOSSES_UBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_UBSMStopWords)[colnames(rawdata_BIOSSES_UBSMStopWords) != "Human"])
colnames(rawdata_MedSTS_UBSMStopWords)[colnames(rawdata_MedSTS_UBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_UBSMStopWords)[colnames(rawdata_MedSTS_UBSMStopWords) != "Human"])
colnames(rawdata_CTR_UBSMStopWords)[colnames(rawdata_CTR_UBSMStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_UBSMStopWords)[colnames(rawdata_CTR_UBSMStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_UBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsCharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMCF)[colnames(rawdata_BIOSSES_UBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_UBSMCF)[colnames(rawdata_BIOSSES_UBSMCF) != "Human"])
colnames(rawdata_MedSTS_UBSMCF)[colnames(rawdata_MedSTS_UBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_UBSMCF)[colnames(rawdata_MedSTS_UBSMCF) != "Human"])
colnames(rawdata_CTR_UBSMCF)[colnames(rawdata_CTR_UBSMCF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_UBSMCF)[colnames(rawdata_CTR_UBSMCF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_UBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsTokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMTOK)[colnames(rawdata_BIOSSES_UBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_UBSMTOK)[colnames(rawdata_BIOSSES_UBSMTOK) != "Human"])
colnames(rawdata_MedSTS_UBSMTOK)[colnames(rawdata_MedSTS_UBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_UBSMTOK)[colnames(rawdata_MedSTS_UBSMTOK) != "Human"])
colnames(rawdata_CTR_UBSMTOK)[colnames(rawdata_CTR_UBSMTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_UBSMTOK)[colnames(rawdata_CTR_UBSMTOK) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_UBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsLC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMLC)[colnames(rawdata_BIOSSES_UBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_UBSMLC)[colnames(rawdata_BIOSSES_UBSMLC) != "Human"])
colnames(rawdata_MedSTS_UBSMLC)[colnames(rawdata_MedSTS_UBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_UBSMLC)[colnames(rawdata_MedSTS_UBSMLC) != "Human"])
colnames(rawdata_CTR_UBSMLC)[colnames(rawdata_CTR_UBSMLC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_UBSMLC)[colnames(rawdata_CTR_UBSMLC) != "Human"])

# NER processing - CTAKES

rawdata_BIOSSES_UBSMNERCtakes <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsNERCtakes.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMNERCtakes <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsNERCtakes.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMNERCtakes <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsNERCtakes.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMNERCtakes)[colnames(rawdata_BIOSSES_UBSMNERCtakes) != "Human"] = paste("[NER]", "", colnames(rawdata_BIOSSES_UBSMNERCtakes)[colnames(rawdata_BIOSSES_UBSMNERCtakes) != "Human"])
colnames(rawdata_MedSTS_UBSMNERCtakes)[colnames(rawdata_MedSTS_UBSMNERCtakes) != "Human"] = paste("[NER]", "", colnames(rawdata_MedSTS_UBSMNERCtakes)[colnames(rawdata_MedSTS_UBSMNERCtakes) != "Human"])
colnames(rawdata_CTR_UBSMNERCtakes)[colnames(rawdata_CTR_UBSMNERCtakes) != "Human"] = paste("[NER]", "", colnames(rawdata_CTR_UBSMNERCtakes)[colnames(rawdata_CTR_UBSMNERCtakes) != "Human"])

# NER processing - MetamapLite

rawdata_BIOSSES_UBSMNERMetamapLite <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsNERMetamapLite.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMNERMetamapLite <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsNERMetamapLite.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMNERMetamapLite <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsNERMetamapLite.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMNERMetamapLite)[colnames(rawdata_BIOSSES_UBSMNERMetamapLite) != "Human"] = paste("[NER]", "", colnames(rawdata_BIOSSES_UBSMNERMetamapLite)[colnames(rawdata_BIOSSES_UBSMNERMetamapLite) != "Human"])
colnames(rawdata_MedSTS_UBSMNERMetamapLite)[colnames(rawdata_MedSTS_UBSMNERMetamapLite) != "Human"] = paste("[NER]", "", colnames(rawdata_MedSTS_UBSMNERMetamapLite)[colnames(rawdata_MedSTS_UBSMNERMetamapLite) != "Human"])
colnames(rawdata_CTR_UBSMNERMetamapLite)[colnames(rawdata_CTR_UBSMNERMetamapLite) != "Human"] = paste("[NER]", "", colnames(rawdata_CTR_UBSMNERMetamapLite)[colnames(rawdata_CTR_UBSMNERMetamapLite) != "Human"])

# NER processing - Metamap

rawdata_BIOSSES_UBSMNERMetamap <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsNERMetamapSNOMEDCT.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMNERMetamap <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsNERMetamapSNOMEDCT.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMNERMetamap <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsNERMetamapSNOMEDCT.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_UBSMNERMetamap)[colnames(rawdata_BIOSSES_UBSMNERMetamap) != "Human"] = paste("[NER]", "", colnames(rawdata_BIOSSES_UBSMNERMetamap)[colnames(rawdata_BIOSSES_UBSMNERMetamap) != "Human"])
colnames(rawdata_MedSTS_UBSMNERMetamap)[colnames(rawdata_MedSTS_UBSMNERMetamap) != "Human"] = paste("[NER]", "", colnames(rawdata_MedSTS_UBSMNERMetamap)[colnames(rawdata_MedSTS_UBSMNERMetamap) != "Human"])
colnames(rawdata_CTR_UBSMNERMetamap)[colnames(rawdata_CTR_UBSMNERMetamap) != "Human"] = paste("[NER]", "", colnames(rawdata_CTR_UBSMNERMetamap)[colnames(rawdata_CTR_UBSMNERMetamap) != "Human"])

# We read the best UBSM measure

rawdata_BIOSSES_UBSMBestMeasure <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_UBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')
rawdata_MedSTS_UBSMBestMeasure  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_UBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')
rawdata_CTR_UBSMBestMeasure     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_UBSM_PreprocessingCombsBestMeasure.csv"), dec = ".", sep = ';')


# We merge the tables into one table for each dataset

UBSM_BIOSSES <- cbind(rawdata_BIOSSES_UBSMStopWords,rawdata_BIOSSES_UBSMCF,rawdata_BIOSSES_UBSMTOK,rawdata_BIOSSES_UBSMLC, rawdata_BIOSSES_UBSMNERCtakes, rawdata_BIOSSES_UBSMNERMetamapLite, rawdata_BIOSSES_UBSMNERMetamap, rawdata_BIOSSES_UBSMBestMeasure)
UBSM_MedSTS <- cbind(rawdata_MedSTS_UBSMStopWords,rawdata_MedSTS_UBSMCF,rawdata_MedSTS_UBSMTOK,rawdata_MedSTS_UBSMLC, rawdata_MedSTS_UBSMNERCtakes, rawdata_MedSTS_UBSMNERMetamapLite, rawdata_MedSTS_UBSMNERMetamap, rawdata_MedSTS_UBSMBestMeasure)
UBSM_CTR <- cbind(rawdata_CTR_UBSMStopWords,rawdata_CTR_UBSMCF,rawdata_CTR_UBSMTOK,rawdata_CTR_UBSMLC, rawdata_CTR_UBSMNERCtakes, rawdata_CTR_UBSMNERMetamapLite, rawdata_CTR_UBSMNERMetamap, rawdata_CTR_UBSMBestMeasure)

rawdata_UBSM <- list( label = "UBSM", biosses = UBSM_BIOSSES, medsts = UBSM_MedSTS, ctr = UBSM_CTR)

