################################
# We load the input raw results file for USE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_USEStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_USEStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_USEStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_USEStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_USEStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_USEStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_USEStopWords)[colnames(rawdata_BIOSSES_USEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_USEStopWords)[colnames(rawdata_BIOSSES_USEStopWords) != "Human"])
colnames(rawdata_MedSTS_USEStopWords)[colnames(rawdata_MedSTS_USEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_USEStopWords)[colnames(rawdata_MedSTS_USEStopWords) != "Human"])
colnames(rawdata_CTR_USEStopWords)[colnames(rawdata_CTR_USEStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_USEStopWords)[colnames(rawdata_CTR_USEStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_USECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_USECharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_USECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_USECharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_USECF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_USECharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_USECF)[colnames(rawdata_BIOSSES_USECF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_USECF)[colnames(rawdata_BIOSSES_USECF) != "Human"])
colnames(rawdata_MedSTS_USECF)[colnames(rawdata_MedSTS_USECF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_USECF)[colnames(rawdata_MedSTS_USECF) != "Human"])
colnames(rawdata_CTR_USECF)[colnames(rawdata_CTR_USECF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_USECF)[colnames(rawdata_CTR_USECF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_USETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_USETokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_USETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_USETokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_USETOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_USETokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_USETOK)[colnames(rawdata_BIOSSES_USETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_USETOK)[colnames(rawdata_BIOSSES_USETOK) != "Human"])
colnames(rawdata_MedSTS_USETOK)[colnames(rawdata_MedSTS_USETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_USETOK)[colnames(rawdata_MedSTS_USETOK) != "Human"])
colnames(rawdata_CTR_USETOK)[colnames(rawdata_CTR_USETOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_USETOK)[colnames(rawdata_CTR_USETOK) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_USELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_USELC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_USELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_USELC.csv"), dec = ".", sep = ';')
rawdata_CTR_USELC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_USELC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_USELC)[colnames(rawdata_BIOSSES_USELC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_USELC)[colnames(rawdata_BIOSSES_USELC) != "Human"])
colnames(rawdata_MedSTS_USELC)[colnames(rawdata_MedSTS_USELC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_USELC)[colnames(rawdata_MedSTS_USELC) != "Human"])
colnames(rawdata_CTR_USELC)[colnames(rawdata_CTR_USELC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_USELC)[colnames(rawdata_CTR_USELC) != "Human"])

# We merge the tables into one table for each dataset

USE_BIOSSES <- cbind(rawdata_BIOSSES_USEStopWords,rawdata_BIOSSES_USECF,rawdata_BIOSSES_USETOK,rawdata_BIOSSES_USELC)
USE_MedSTS <- cbind(rawdata_MedSTS_USEStopWords,rawdata_MedSTS_USECF,rawdata_MedSTS_USETOK,rawdata_MedSTS_USELC)
USE_CTR <- cbind(rawdata_CTR_USEStopWords,rawdata_CTR_USECF,rawdata_CTR_USETOK,rawdata_CTR_USELC)

rawdata_USE <- list( label = "USE", biosses = USE_BIOSSES, medsts = USE_MedSTS, ctr = USE_CTR)