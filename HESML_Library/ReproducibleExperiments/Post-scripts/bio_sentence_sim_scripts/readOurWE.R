################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_OurWEStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_OurWEStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_OurWEStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_OurWEStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_OurWEStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_OurWEStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_OurWEStopWords)[colnames(rawdata_BIOSSES_OurWEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_OurWEStopWords)[colnames(rawdata_BIOSSES_OurWEStopWords) != "Human"])
colnames(rawdata_MedSTS_OurWEStopWords)[colnames(rawdata_MedSTS_OurWEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_OurWEStopWords)[colnames(rawdata_MedSTS_OurWEStopWords) != "Human"])
colnames(rawdata_CTR_OurWEStopWords)[colnames(rawdata_CTR_OurWEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_OurWEStopWords)[colnames(rawdata_CTR_OurWEStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_OurWECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_OurWECharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_OurWECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_OurWECharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_OurWECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_OurWECharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_OurWECF)[colnames(rawdata_BIOSSES_OurWECF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_OurWECF)[colnames(rawdata_BIOSSES_OurWECF) != "Human"])
colnames(rawdata_MedSTS_OurWECF)[colnames(rawdata_MedSTS_OurWECF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_OurWECF)[colnames(rawdata_MedSTS_OurWECF) != "Human"])
colnames(rawdata_CTR_OurWECF)[colnames(rawdata_CTR_OurWECF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_OurWECF)[colnames(rawdata_CTR_OurWECF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_OurWETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_OurWETokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_OurWETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_OurWETokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_OurWETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_OurWETokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_OurWETOK)[colnames(rawdata_BIOSSES_OurWETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_OurWETOK)[colnames(rawdata_BIOSSES_OurWETOK) != "Human"])
colnames(rawdata_MedSTS_OurWETOK)[colnames(rawdata_MedSTS_OurWETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_OurWETOK)[colnames(rawdata_MedSTS_OurWETOK) != "Human"])
colnames(rawdata_CTR_OurWETOK)[colnames(rawdata_CTR_OurWETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_OurWETOK)[colnames(rawdata_CTR_OurWETOK) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_OurWELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_OurWELC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_OurWELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_OurWELC.csv"), dec = ".", sep = ';')
rawdata_CTR_OurWELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_OurWELC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_OurWELC)[colnames(rawdata_BIOSSES_OurWELC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_OurWELC)[colnames(rawdata_BIOSSES_OurWELC) != "Human"])
colnames(rawdata_MedSTS_OurWELC)[colnames(rawdata_MedSTS_OurWELC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_OurWELC)[colnames(rawdata_MedSTS_OurWELC) != "Human"])
colnames(rawdata_CTR_OurWELC)[colnames(rawdata_CTR_OurWELC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_OurWELC)[colnames(rawdata_CTR_OurWELC) != "Human"])


# We merge the tables into one table for each dataset

OurWE_BIOSSES <- cbind(rawdata_BIOSSES_OurWEStopWords,rawdata_BIOSSES_OurWECF,rawdata_BIOSSES_OurWETOK,rawdata_BIOSSES_OurWELC)
OurWE_MedSTS <- cbind(rawdata_MedSTS_OurWEStopWords,rawdata_MedSTS_OurWECF,rawdata_MedSTS_OurWETOK,rawdata_MedSTS_OurWELC)
OurWE_CTR <- cbind(rawdata_CTR_OurWEStopWords,rawdata_CTR_OurWECF,rawdata_CTR_OurWETOK,rawdata_CTR_OurWELC)

rawdata_OurWE <- list( label = "OurWE", biosses = OurWE_BIOSSES, medsts = OurWE_MedSTS, ctr = OurWE_CTR)