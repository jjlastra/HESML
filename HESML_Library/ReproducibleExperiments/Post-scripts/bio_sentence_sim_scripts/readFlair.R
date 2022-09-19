################################
# We load the input raw results file for Flair-based measures
################################

# Stop-words processing

rawdata_BIOSSES_FlairStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_FlairStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_FlairStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_FlairStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_FlairStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_FlairStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_FlairStopWords)[colnames(rawdata_BIOSSES_FlairStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_FlairStopWords)[colnames(rawdata_BIOSSES_FlairStopWords) != "Human"])
colnames(rawdata_MedSTS_FlairStopWords)[colnames(rawdata_MedSTS_FlairStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_FlairStopWords)[colnames(rawdata_MedSTS_FlairStopWords) != "Human"])
colnames(rawdata_CTR_FlairStopWords)[colnames(rawdata_CTR_FlairStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_FlairStopWords)[colnames(rawdata_CTR_FlairStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_FlairCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_FlairCharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_FlairCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_FlairCharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_FlairCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_FlairCharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_FlairCF)[colnames(rawdata_BIOSSES_FlairCF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_FlairCF)[colnames(rawdata_BIOSSES_FlairCF) != "Human"])
colnames(rawdata_MedSTS_FlairCF)[colnames(rawdata_MedSTS_FlairCF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_FlairCF)[colnames(rawdata_MedSTS_FlairCF) != "Human"])
colnames(rawdata_CTR_FlairCF)[colnames(rawdata_CTR_FlairCF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_FlairCF)[colnames(rawdata_CTR_FlairCF) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_FlairTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_FlairTokenizer.csv"), dec = ".", sep = ';')
rawdata_MedSTS_FlairTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_FlairTokenizer.csv"), dec = ".", sep = ';')
rawdata_CTR_FlairTOK <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_FlairTokenizer.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_FlairTOK)[colnames(rawdata_BIOSSES_FlairTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_FlairTOK)[colnames(rawdata_BIOSSES_FlairTOK) != "Human"])
colnames(rawdata_MedSTS_FlairTOK)[colnames(rawdata_MedSTS_FlairTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_FlairTOK)[colnames(rawdata_MedSTS_FlairTOK) != "Human"])
colnames(rawdata_CTR_FlairTOK)[colnames(rawdata_CTR_FlairTOK) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_FlairTOK)[colnames(rawdata_CTR_FlairTOK) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_FlairLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_FlairLC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_FlairLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_FlairLC.csv"), dec = ".", sep = ';')
rawdata_CTR_FlairLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_FlairLC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_FlairLC)[colnames(rawdata_BIOSSES_FlairLC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_FlairLC)[colnames(rawdata_BIOSSES_FlairLC) != "Human"])
colnames(rawdata_MedSTS_FlairLC)[colnames(rawdata_MedSTS_FlairLC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_FlairLC)[colnames(rawdata_MedSTS_FlairLC) != "Human"])
colnames(rawdata_CTR_FlairLC)[colnames(rawdata_CTR_FlairLC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_FlairLC)[colnames(rawdata_CTR_FlairLC) != "Human"])

# We merge the tables into one table for each dataset

Flair_BIOSSES <- cbind(rawdata_BIOSSES_FlairStopWords,rawdata_BIOSSES_FlairCF,rawdata_BIOSSES_FlairTOK,rawdata_BIOSSES_FlairLC)
Flair_MedSTS <- cbind(rawdata_MedSTS_FlairStopWords,rawdata_MedSTS_FlairCF,rawdata_MedSTS_FlairTOK,rawdata_MedSTS_FlairLC)
Flair_CTR <- cbind(rawdata_CTR_FlairStopWords,rawdata_CTR_FlairCF,rawdata_CTR_FlairTOK,rawdata_CTR_FlairLC)

rawdata_Flair <- list( label = "Flair", biosses = Flair_BIOSSES, medsts = Flair_MedSTS, ctr = Flair_CTR)