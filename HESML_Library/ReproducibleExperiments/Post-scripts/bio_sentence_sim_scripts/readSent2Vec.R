################################
# We load the input raw results file for Sent2Vec-based measures
################################

# Stop-words processing

rawdata_BIOSSES_Sent2VecStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Sent2VecStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_Sent2VecStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Sent2VecStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_Sent2VecStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Sent2VecStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_Sent2VecStopWords)[colnames(rawdata_BIOSSES_Sent2VecStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_Sent2VecStopWords)[colnames(rawdata_BIOSSES_Sent2VecStopWords) != "Human"])
colnames(rawdata_MedSTS_Sent2VecStopWords)[colnames(rawdata_MedSTS_Sent2VecStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_Sent2VecStopWords)[colnames(rawdata_MedSTS_Sent2VecStopWords) != "Human"])
colnames(rawdata_CTR_Sent2VecStopWords)[colnames(rawdata_CTR_Sent2VecStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_Sent2VecStopWords)[colnames(rawdata_CTR_Sent2VecStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_Sent2VecCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Sent2VecCharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_Sent2VecCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Sent2VecCharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_Sent2VecCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Sent2VecCharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_Sent2VecCF)[colnames(rawdata_BIOSSES_Sent2VecCF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_Sent2VecCF)[colnames(rawdata_BIOSSES_Sent2VecCF) != "Human"])
colnames(rawdata_MedSTS_Sent2VecCF)[colnames(rawdata_MedSTS_Sent2VecCF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_Sent2VecCF)[colnames(rawdata_MedSTS_Sent2VecCF) != "Human"])
colnames(rawdata_CTR_Sent2VecCF)[colnames(rawdata_CTR_Sent2VecCF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_Sent2VecCF)[colnames(rawdata_CTR_Sent2VecCF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_Sent2VecTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Sent2VecTokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_Sent2VecTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Sent2VecTokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_Sent2VecTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Sent2VecTokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_Sent2VecTOK)[colnames(rawdata_BIOSSES_Sent2VecTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_Sent2VecTOK)[colnames(rawdata_BIOSSES_Sent2VecTOK) != "Human"])
colnames(rawdata_MedSTS_Sent2VecTOK)[colnames(rawdata_MedSTS_Sent2VecTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_Sent2VecTOK)[colnames(rawdata_MedSTS_Sent2VecTOK) != "Human"])
colnames(rawdata_CTR_Sent2VecTOK)[colnames(rawdata_CTR_Sent2VecTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_Sent2VecTOK)[colnames(rawdata_CTR_Sent2VecTOK) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_Sent2VecLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_Sent2VecLC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_Sent2VecLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_Sent2VecLC.csv"), dec = ".", sep = ';')
rawdata_CTR_Sent2VecLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_Sent2VecLC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_Sent2VecLC)[colnames(rawdata_BIOSSES_Sent2VecLC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_Sent2VecLC)[colnames(rawdata_BIOSSES_Sent2VecLC) != "Human"])
colnames(rawdata_MedSTS_Sent2VecLC)[colnames(rawdata_MedSTS_Sent2VecLC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_Sent2VecLC)[colnames(rawdata_MedSTS_Sent2VecLC) != "Human"])
colnames(rawdata_CTR_Sent2VecLC)[colnames(rawdata_CTR_Sent2VecLC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_Sent2VecLC)[colnames(rawdata_CTR_Sent2VecLC) != "Human"])

# We merge the tables into one table for each dataset

Sent2Vec_BIOSSES <- cbind(rawdata_BIOSSES_Sent2VecStopWords,rawdata_BIOSSES_Sent2VecCF,rawdata_BIOSSES_Sent2VecTOK,rawdata_BIOSSES_Sent2VecLC)
Sent2Vec_MedSTS <- cbind(rawdata_MedSTS_Sent2VecStopWords,rawdata_MedSTS_Sent2VecCF,rawdata_MedSTS_Sent2VecTOK,rawdata_MedSTS_Sent2VecLC)
Sent2Vec_CTR <- cbind(rawdata_CTR_Sent2VecStopWords,rawdata_CTR_Sent2VecCF,rawdata_CTR_Sent2VecTOK,rawdata_CTR_Sent2VecLC)

rawdata_Sent2Vec <- list( label = "Sent2Vec", biosses = Sent2Vec_BIOSSES, medsts = Sent2Vec_MedSTS, ctr = Sent2Vec_CTR)