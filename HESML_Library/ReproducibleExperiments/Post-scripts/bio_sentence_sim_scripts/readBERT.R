################################
# We load the input raw results file for BERT-based measures
################################

# Stop-words processing

rawdata_BIOSSES_BERTStopWords <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_BERTStopWords.csv"), dec = ".", sep = ';')
rawdata_MedSTS_BERTStopWords  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_BERTStopWords.csv"), dec = ".", sep = ';')
rawdata_CTR_BERTStopWords     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_BERTStopWords.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_BERTStopWords)[colnames(rawdata_BIOSSES_BERTStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_BERTStopWords)[colnames(rawdata_BIOSSES_BERTStopWords) != "Human"])
colnames(rawdata_MedSTS_BERTStopWords)[colnames(rawdata_MedSTS_BERTStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_BERTStopWords)[colnames(rawdata_MedSTS_BERTStopWords) != "Human"])
colnames(rawdata_CTR_BERTStopWords)[colnames(rawdata_CTR_BERTStopWords) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_BERTStopWords)[colnames(rawdata_CTR_BERTStopWords) != "Human"])

# Char filtering processing

rawdata_BIOSSES_BERTCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_BERTCharFiltering.csv"), dec = ".", sep = ';')
rawdata_MedSTS_BERTCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_BERTCharFiltering.csv"), dec = ".", sep = ';')
rawdata_CTR_BERTCF <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_BERTCharFiltering.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_BERTCF)[colnames(rawdata_BIOSSES_BERTCF) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_BERTCF)[colnames(rawdata_BIOSSES_BERTCF) != "Human"])
colnames(rawdata_MedSTS_BERTCF)[colnames(rawdata_MedSTS_BERTCF) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_BERTCF)[colnames(rawdata_MedSTS_BERTCF) != "Human"])
colnames(rawdata_CTR_BERTCF)[colnames(rawdata_CTR_BERTCF) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_BERTCF)[colnames(rawdata_CTR_BERTCF) != "Human"])

# Lowercasing processing

rawdata_BIOSSES_BERTLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_BERTLC.csv"), dec = ".", sep = ';')
rawdata_MedSTS_BERTLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_BERTLC.csv"), dec = ".", sep = ';')
rawdata_CTR_BERTLC <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_BERTLC.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_BERTLC)[colnames(rawdata_BIOSSES_BERTLC) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_BERTLC)[colnames(rawdata_BIOSSES_BERTLC) != "Human"])
colnames(rawdata_MedSTS_BERTLC)[colnames(rawdata_MedSTS_BERTLC) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_BERTLC)[colnames(rawdata_MedSTS_BERTLC) != "Human"])
colnames(rawdata_CTR_BERTLC)[colnames(rawdata_CTR_BERTLC) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_BERTLC)[colnames(rawdata_CTR_BERTLC) != "Human"])

# We merge the tables into one table for each dataset

BERT_BIOSSES <- cbind(rawdata_BIOSSES_BERTStopWords,rawdata_BIOSSES_BERTCF,rawdata_BIOSSES_BERTLC)
BERT_MedSTS <- cbind(rawdata_MedSTS_BERTStopWords,rawdata_MedSTS_BERTCF,rawdata_MedSTS_BERTLC)
BERT_CTR <- cbind(rawdata_CTR_BERTStopWords,rawdata_CTR_BERTCF,rawdata_CTR_BERTLC)

rawdata_BERT <- list( label = "BERT", biosses = BERT_BIOSSES, medsts = BERT_MedSTS, ctr = BERT_CTR)