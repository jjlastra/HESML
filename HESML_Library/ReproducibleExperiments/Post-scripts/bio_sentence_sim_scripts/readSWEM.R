################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part1Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part1Max  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part1Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part1Max     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part1Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part1Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Max) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part1Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Max) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part1Max)[colnames(rawdata_MedSTS_SWEMStopWords_part1Max) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part1Max)[colnames(rawdata_MedSTS_SWEMStopWords_part1Max) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part1Max)[colnames(rawdata_CTR_SWEMStopWords_part1Max) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part1Max)[colnames(rawdata_CTR_SWEMStopWords_part1Max) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part1Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part1Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part1Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Max) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Max) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part1Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Max) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part1Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Max) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part1Max)[colnames(rawdata_CTR_SWEMCharFiltering_part1Max) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part1Max)[colnames(rawdata_CTR_SWEMCharFiltering_part1Max) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part1Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part1Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part1Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part1Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part1Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Max) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part1Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part1Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Max) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part1Max)[colnames(rawdata_CTR_SWEMTokenizer_part1Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part1Max)[colnames(rawdata_CTR_SWEMTokenizer_part1Max) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part1Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part1Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part1Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part1Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part1Max)[colnames(rawdata_BIOSSES_SWEMLC_part1Max) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part1Max)[colnames(rawdata_BIOSSES_SWEMLC_part1Max) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part1Max)[colnames(rawdata_MedSTS_SWEMLC_part1Max) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part1Max)[colnames(rawdata_MedSTS_SWEMLC_part1Max) != "Human"])
colnames(rawdata_CTR_SWEMLC_part1Max)[colnames(rawdata_CTR_SWEMLC_part1Max) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part1Max)[colnames(rawdata_CTR_SWEMLC_part1Max) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part2Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part2Max  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part2Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part2Max     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part2Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part2Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Max) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part2Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Max) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part2Max)[colnames(rawdata_MedSTS_SWEMStopWords_part2Max) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part2Max)[colnames(rawdata_MedSTS_SWEMStopWords_part2Max) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part2Max)[colnames(rawdata_CTR_SWEMStopWords_part2Max) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part2Max)[colnames(rawdata_CTR_SWEMStopWords_part2Max) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part2Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part2Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part2Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Max) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Max) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part2Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Max) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part2Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Max) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part2Max)[colnames(rawdata_CTR_SWEMCharFiltering_part2Max) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part2Max)[colnames(rawdata_CTR_SWEMCharFiltering_part2Max) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part2Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part2Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part2Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part2Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part2Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Max) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part2Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part2Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Max) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part2Max)[colnames(rawdata_CTR_SWEMTokenizer_part2Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part2Max)[colnames(rawdata_CTR_SWEMTokenizer_part2Max) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part2Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part2Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part2Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part2Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part2Max)[colnames(rawdata_BIOSSES_SWEMLC_part2Max) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part2Max)[colnames(rawdata_BIOSSES_SWEMLC_part2Max) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part2Max)[colnames(rawdata_MedSTS_SWEMLC_part2Max) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part2Max)[colnames(rawdata_MedSTS_SWEMLC_part2Max) != "Human"])
colnames(rawdata_CTR_SWEMLC_part2Max)[colnames(rawdata_CTR_SWEMLC_part2Max) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part2Max)[colnames(rawdata_CTR_SWEMLC_part2Max) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part3Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part3Max  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part3Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part3Max     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part3Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part3Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Max) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part3Max)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Max) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part3Max)[colnames(rawdata_MedSTS_SWEMStopWords_part3Max) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part3Max)[colnames(rawdata_MedSTS_SWEMStopWords_part3Max) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part3Max)[colnames(rawdata_CTR_SWEMStopWords_part3Max) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part3Max)[colnames(rawdata_CTR_SWEMStopWords_part3Max) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part3Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part3Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part3Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Max) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Max)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Max) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part3Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Max) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part3Max)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Max) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part3Max)[colnames(rawdata_CTR_SWEMCharFiltering_part3Max) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part3Max)[colnames(rawdata_CTR_SWEMCharFiltering_part3Max) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part3Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part3Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part3Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part3Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part3Max)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Max) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part3Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part3Max)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Max) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part3Max)[colnames(rawdata_CTR_SWEMTokenizer_part3Max) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part3Max)[colnames(rawdata_CTR_SWEMTokenizer_part3Max) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part3Max.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part3Max.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part3Max <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part3Max.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part3Max)[colnames(rawdata_BIOSSES_SWEMLC_part3Max) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part3Max)[colnames(rawdata_BIOSSES_SWEMLC_part3Max) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part3Max)[colnames(rawdata_MedSTS_SWEMLC_part3Max) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part3Max)[colnames(rawdata_MedSTS_SWEMLC_part3Max) != "Human"])
colnames(rawdata_CTR_SWEMLC_part3Max)[colnames(rawdata_CTR_SWEMLC_part3Max) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part3Max)[colnames(rawdata_CTR_SWEMLC_part3Max) != "Human"])






################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part1Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part1Min  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part1Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part1Min     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part1Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part1Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Min) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part1Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Min) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part1Min)[colnames(rawdata_MedSTS_SWEMStopWords_part1Min) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part1Min)[colnames(rawdata_MedSTS_SWEMStopWords_part1Min) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part1Min)[colnames(rawdata_CTR_SWEMStopWords_part1Min) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part1Min)[colnames(rawdata_CTR_SWEMStopWords_part1Min) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part1Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part1Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part1Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Min) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Min) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part1Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Min) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part1Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Min) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part1Min)[colnames(rawdata_CTR_SWEMCharFiltering_part1Min) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part1Min)[colnames(rawdata_CTR_SWEMCharFiltering_part1Min) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part1Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part1Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part1Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part1Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part1Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Min) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part1Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part1Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Min) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part1Min)[colnames(rawdata_CTR_SWEMTokenizer_part1Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part1Min)[colnames(rawdata_CTR_SWEMTokenizer_part1Min) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part1Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part1Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part1Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part1Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part1Min)[colnames(rawdata_BIOSSES_SWEMLC_part1Min) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part1Min)[colnames(rawdata_BIOSSES_SWEMLC_part1Min) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part1Min)[colnames(rawdata_MedSTS_SWEMLC_part1Min) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part1Min)[colnames(rawdata_MedSTS_SWEMLC_part1Min) != "Human"])
colnames(rawdata_CTR_SWEMLC_part1Min)[colnames(rawdata_CTR_SWEMLC_part1Min) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part1Min)[colnames(rawdata_CTR_SWEMLC_part1Min) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part2Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part2Min  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part2Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part2Min     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part2Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part2Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Min) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part2Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Min) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part2Min)[colnames(rawdata_MedSTS_SWEMStopWords_part2Min) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part2Min)[colnames(rawdata_MedSTS_SWEMStopWords_part2Min) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part2Min)[colnames(rawdata_CTR_SWEMStopWords_part2Min) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part2Min)[colnames(rawdata_CTR_SWEMStopWords_part2Min) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part2Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part2Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part2Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Min) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Min) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part2Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Min) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part2Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Min) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part2Min)[colnames(rawdata_CTR_SWEMCharFiltering_part2Min) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part2Min)[colnames(rawdata_CTR_SWEMCharFiltering_part2Min) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part2Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part2Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part2Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part2Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part2Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Min) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part2Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part2Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Min) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part2Min)[colnames(rawdata_CTR_SWEMTokenizer_part2Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part2Min)[colnames(rawdata_CTR_SWEMTokenizer_part2Min) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part2Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part2Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part2Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part2Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part2Min)[colnames(rawdata_BIOSSES_SWEMLC_part2Min) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part2Min)[colnames(rawdata_BIOSSES_SWEMLC_part2Min) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part2Min)[colnames(rawdata_MedSTS_SWEMLC_part2Min) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part2Min)[colnames(rawdata_MedSTS_SWEMLC_part2Min) != "Human"])
colnames(rawdata_CTR_SWEMLC_part2Min)[colnames(rawdata_CTR_SWEMLC_part2Min) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part2Min)[colnames(rawdata_CTR_SWEMLC_part2Min) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part3Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part3Min  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part3Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part3Min     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part3Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part3Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Min) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part3Min)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Min) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part3Min)[colnames(rawdata_MedSTS_SWEMStopWords_part3Min) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part3Min)[colnames(rawdata_MedSTS_SWEMStopWords_part3Min) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part3Min)[colnames(rawdata_CTR_SWEMStopWords_part3Min) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part3Min)[colnames(rawdata_CTR_SWEMStopWords_part3Min) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part3Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part3Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part3Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Min) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Min)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Min) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part3Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Min) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part3Min)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Min) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part3Min)[colnames(rawdata_CTR_SWEMCharFiltering_part3Min) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part3Min)[colnames(rawdata_CTR_SWEMCharFiltering_part3Min) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part3Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part3Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part3Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part3Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part3Min)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Min) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part3Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part3Min)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Min) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part3Min)[colnames(rawdata_CTR_SWEMTokenizer_part3Min) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part3Min)[colnames(rawdata_CTR_SWEMTokenizer_part3Min) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part3Min.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part3Min.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part3Min <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part3Min.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part3Min)[colnames(rawdata_BIOSSES_SWEMLC_part3Min) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part3Min)[colnames(rawdata_BIOSSES_SWEMLC_part3Min) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part3Min)[colnames(rawdata_MedSTS_SWEMLC_part3Min) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part3Min)[colnames(rawdata_MedSTS_SWEMLC_part3Min) != "Human"])
colnames(rawdata_CTR_SWEMLC_part3Min)[colnames(rawdata_CTR_SWEMLC_part3Min) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part3Min)[colnames(rawdata_CTR_SWEMLC_part3Min) != "Human"])


################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part1Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part1Average  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part1Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part1Average     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part1Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part1Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Average) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part1Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Average) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part1Average)[colnames(rawdata_MedSTS_SWEMStopWords_part1Average) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part1Average)[colnames(rawdata_MedSTS_SWEMStopWords_part1Average) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part1Average)[colnames(rawdata_CTR_SWEMStopWords_part1Average) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part1Average)[colnames(rawdata_CTR_SWEMStopWords_part1Average) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part1Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part1Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part1Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Average) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Average) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part1Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Average) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part1Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Average) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part1Average)[colnames(rawdata_CTR_SWEMCharFiltering_part1Average) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part1Average)[colnames(rawdata_CTR_SWEMCharFiltering_part1Average) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part1Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part1Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part1Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part1Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part1Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Average) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part1Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part1Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Average) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part1Average)[colnames(rawdata_CTR_SWEMTokenizer_part1Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part1Average)[colnames(rawdata_CTR_SWEMTokenizer_part1Average) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part1Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part1Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part1Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part1Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part1Average)[colnames(rawdata_BIOSSES_SWEMLC_part1Average) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part1Average)[colnames(rawdata_BIOSSES_SWEMLC_part1Average) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part1Average)[colnames(rawdata_MedSTS_SWEMLC_part1Average) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part1Average)[colnames(rawdata_MedSTS_SWEMLC_part1Average) != "Human"])
colnames(rawdata_CTR_SWEMLC_part1Average)[colnames(rawdata_CTR_SWEMLC_part1Average) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part1Average)[colnames(rawdata_CTR_SWEMLC_part1Average) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part2Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part2Average  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part2Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part2Average     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part2Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part2Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Average) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part2Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Average) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part2Average)[colnames(rawdata_MedSTS_SWEMStopWords_part2Average) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part2Average)[colnames(rawdata_MedSTS_SWEMStopWords_part2Average) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part2Average)[colnames(rawdata_CTR_SWEMStopWords_part2Average) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part2Average)[colnames(rawdata_CTR_SWEMStopWords_part2Average) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part2Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part2Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part2Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Average) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Average) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part2Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Average) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part2Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Average) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part2Average)[colnames(rawdata_CTR_SWEMCharFiltering_part2Average) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part2Average)[colnames(rawdata_CTR_SWEMCharFiltering_part2Average) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part2Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part2Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part2Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part2Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part2Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Average) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part2Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part2Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Average) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part2Average)[colnames(rawdata_CTR_SWEMTokenizer_part2Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part2Average)[colnames(rawdata_CTR_SWEMTokenizer_part2Average) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part2Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part2Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part2Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part2Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part2Average)[colnames(rawdata_BIOSSES_SWEMLC_part2Average) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part2Average)[colnames(rawdata_BIOSSES_SWEMLC_part2Average) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part2Average)[colnames(rawdata_MedSTS_SWEMLC_part2Average) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part2Average)[colnames(rawdata_MedSTS_SWEMLC_part2Average) != "Human"])
colnames(rawdata_CTR_SWEMLC_part2Average)[colnames(rawdata_CTR_SWEMLC_part2Average) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part2Average)[colnames(rawdata_CTR_SWEMLC_part2Average) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part3Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part3Average  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part3Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part3Average     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part3Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part3Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Average) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part3Average)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Average) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part3Average)[colnames(rawdata_MedSTS_SWEMStopWords_part3Average) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part3Average)[colnames(rawdata_MedSTS_SWEMStopWords_part3Average) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part3Average)[colnames(rawdata_CTR_SWEMStopWords_part3Average) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part3Average)[colnames(rawdata_CTR_SWEMStopWords_part3Average) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part3Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part3Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part3Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Average) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Average)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Average) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part3Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Average) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part3Average)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Average) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part3Average)[colnames(rawdata_CTR_SWEMCharFiltering_part3Average) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part3Average)[colnames(rawdata_CTR_SWEMCharFiltering_part3Average) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part3Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part3Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part3Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part3Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part3Average)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Average) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part3Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part3Average)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Average) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part3Average)[colnames(rawdata_CTR_SWEMTokenizer_part3Average) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part3Average)[colnames(rawdata_CTR_SWEMTokenizer_part3Average) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part3Average.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part3Average.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part3Average <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part3Average.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part3Average)[colnames(rawdata_BIOSSES_SWEMLC_part3Average) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part3Average)[colnames(rawdata_BIOSSES_SWEMLC_part3Average) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part3Average)[colnames(rawdata_MedSTS_SWEMLC_part3Average) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part3Average)[colnames(rawdata_MedSTS_SWEMLC_part3Average) != "Human"])
colnames(rawdata_CTR_SWEMLC_part3Average)[colnames(rawdata_CTR_SWEMLC_part3Average) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part3Average)[colnames(rawdata_CTR_SWEMLC_part3Average) != "Human"])




################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part1Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part1Sum  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part1Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part1Sum     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part1Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part1Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part1Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part1Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part1Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part1Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part1Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part1Sum) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part1Sum)[colnames(rawdata_CTR_SWEMStopWords_part1Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part1Sum)[colnames(rawdata_CTR_SWEMStopWords_part1Sum) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part1Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part1Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part1Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part1Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part1Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part1Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part1Sum) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part1Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part1Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part1Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part1Sum) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part1Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part1Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part1Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part1Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part1Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part1Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part1Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part1Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part1Sum) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part1Sum)[colnames(rawdata_CTR_SWEMTokenizer_part1Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part1Sum)[colnames(rawdata_CTR_SWEMTokenizer_part1Sum) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part1Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part1Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part1Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part1Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part1Sum)[colnames(rawdata_BIOSSES_SWEMLC_part1Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part1Sum)[colnames(rawdata_BIOSSES_SWEMLC_part1Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part1Sum)[colnames(rawdata_MedSTS_SWEMLC_part1Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part1Sum)[colnames(rawdata_MedSTS_SWEMLC_part1Sum) != "Human"])
colnames(rawdata_CTR_SWEMLC_part1Sum)[colnames(rawdata_CTR_SWEMLC_part1Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part1Sum)[colnames(rawdata_CTR_SWEMLC_part1Sum) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part2Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part2Sum  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part2Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part2Sum     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part2Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part2Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part2Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part2Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part2Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part2Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part2Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part2Sum) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part2Sum)[colnames(rawdata_CTR_SWEMStopWords_part2Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part2Sum)[colnames(rawdata_CTR_SWEMStopWords_part2Sum) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part2Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part2Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part2Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part2Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part2Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part2Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part2Sum) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part2Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part2Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part2Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part2Sum) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part2Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part2Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part2Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part2Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part2Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part2Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part2Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part2Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part2Sum) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part2Sum)[colnames(rawdata_CTR_SWEMTokenizer_part2Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part2Sum)[colnames(rawdata_CTR_SWEMTokenizer_part2Sum) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part2Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part2Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part2Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part2Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part2Sum)[colnames(rawdata_BIOSSES_SWEMLC_part2Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part2Sum)[colnames(rawdata_BIOSSES_SWEMLC_part2Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part2Sum)[colnames(rawdata_MedSTS_SWEMLC_part2Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part2Sum)[colnames(rawdata_MedSTS_SWEMLC_part2Sum) != "Human"])
colnames(rawdata_CTR_SWEMLC_part2Sum)[colnames(rawdata_CTR_SWEMLC_part2Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part2Sum)[colnames(rawdata_CTR_SWEMLC_part2Sum) != "Human"])

################################
# We load the input raw results file for OurWE-based measures
################################

# Stop-words processing

rawdata_BIOSSES_SWEMStopWords_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMStopWords_part3Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMStopWords_part3Sum  <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMStopWords_part3Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMStopWords_part3Sum     <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMStopWords_part3Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMStopWords_part3Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_BIOSSES_SWEMStopWords_part3Sum)[colnames(rawdata_BIOSSES_SWEMStopWords_part3Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMStopWords_part3Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part3Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_MedSTS_SWEMStopWords_part3Sum)[colnames(rawdata_MedSTS_SWEMStopWords_part3Sum) != "Human"])
colnames(rawdata_CTR_SWEMStopWords_part3Sum)[colnames(rawdata_CTR_SWEMStopWords_part3Sum) != "Human"] = paste("[SW]", "", colnames(rawdata_CTR_SWEMStopWords_part3Sum)[colnames(rawdata_CTR_SWEMStopWords_part3Sum) != "Human"])

# Char filtering processing

rawdata_BIOSSES_SWEMCharFiltering_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMCharFiltering_part3Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMCharFiltering_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMCharFiltering_part3Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMCharFiltering_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMCharFiltering_part3Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Sum)[colnames(rawdata_BIOSSES_SWEMCharFiltering_part3Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMCharFiltering_part3Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_MedSTS_SWEMCharFiltering_part3Sum)[colnames(rawdata_MedSTS_SWEMCharFiltering_part3Sum) != "Human"])
colnames(rawdata_CTR_SWEMCharFiltering_part3Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part3Sum) != "Human"] = paste("[CF]", "", colnames(rawdata_CTR_SWEMCharFiltering_part3Sum)[colnames(rawdata_CTR_SWEMCharFiltering_part3Sum) != "Human"])

# Tokenizers processing

rawdata_BIOSSES_SWEMTokenizer_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMTokenizer_part3Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMTokenizer_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMTokenizer_part3Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMTokenizer_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMTokenizer_part3Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMTokenizer_part3Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_BIOSSES_SWEMTokenizer_part3Sum)[colnames(rawdata_BIOSSES_SWEMTokenizer_part3Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMTokenizer_part3Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_MedSTS_SWEMTokenizer_part3Sum)[colnames(rawdata_MedSTS_SWEMTokenizer_part3Sum) != "Human"])
colnames(rawdata_CTR_SWEMTokenizer_part3Sum)[colnames(rawdata_CTR_SWEMTokenizer_part3Sum) != "Human"] = paste("[TOK]", "", colnames(rawdata_CTR_SWEMTokenizer_part3Sum)[colnames(rawdata_CTR_SWEMTokenizer_part3Sum) != "Human"])


# Lowercasing processing

rawdata_BIOSSES_SWEMLC_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_BIOSSES_SWEMLC_part3Sum.csv"), dec = ".", sep = ';')
rawdata_MedSTS_SWEMLC_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_MedSTSFull_SWEMLC_part3Sum.csv"), dec = ".", sep = ';')
rawdata_CTR_SWEMLC_part3Sum <- read.csv(paste(inputDir, sep = "", "raw_similarity_CTR_SWEMLC_part3Sum.csv"), dec = ".", sep = ';')

# Replace the names of the columns with the current type of experiment (SW: stopwords, CF: char filtering, TOK: tokenizers, LW: lowercasing)

colnames(rawdata_BIOSSES_SWEMLC_part3Sum)[colnames(rawdata_BIOSSES_SWEMLC_part3Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_BIOSSES_SWEMLC_part3Sum)[colnames(rawdata_BIOSSES_SWEMLC_part3Sum) != "Human"])
colnames(rawdata_MedSTS_SWEMLC_part3Sum)[colnames(rawdata_MedSTS_SWEMLC_part3Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_MedSTS_SWEMLC_part3Sum)[colnames(rawdata_MedSTS_SWEMLC_part3Sum) != "Human"])
colnames(rawdata_CTR_SWEMLC_part3Sum)[colnames(rawdata_CTR_SWEMLC_part3Sum) != "Human"] = paste("[LC]", "", colnames(rawdata_CTR_SWEMLC_part3Sum)[colnames(rawdata_CTR_SWEMLC_part3Sum) != "Human"])









# We merge the tables into one table for each dataset

SWEM_BIOSSES <- cbind(rawdata_BIOSSES_SWEMStopWords_part1Max, rawdata_BIOSSES_SWEMCharFiltering_part1Max, rawdata_BIOSSES_SWEMTokenizer_part1Max, rawdata_BIOSSES_SWEMLC_part1Max, rawdata_BIOSSES_SWEMStopWords_part2Max, rawdata_BIOSSES_SWEMCharFiltering_part2Max, rawdata_BIOSSES_SWEMTokenizer_part2Max, rawdata_BIOSSES_SWEMLC_part2Max,rawdata_BIOSSES_SWEMStopWords_part3Max, rawdata_BIOSSES_SWEMCharFiltering_part3Max, rawdata_BIOSSES_SWEMTokenizer_part3Max, rawdata_BIOSSES_SWEMLC_part3Max,rawdata_BIOSSES_SWEMStopWords_part1Min, rawdata_BIOSSES_SWEMCharFiltering_part1Min, rawdata_BIOSSES_SWEMTokenizer_part1Min, rawdata_BIOSSES_SWEMLC_part1Min,rawdata_BIOSSES_SWEMStopWords_part2Min, rawdata_BIOSSES_SWEMCharFiltering_part2Min, rawdata_BIOSSES_SWEMTokenizer_part2Min, rawdata_BIOSSES_SWEMLC_part2Min,rawdata_BIOSSES_SWEMStopWords_part3Min, rawdata_BIOSSES_SWEMCharFiltering_part3Min, rawdata_BIOSSES_SWEMTokenizer_part3Min, rawdata_BIOSSES_SWEMLC_part3Min,rawdata_BIOSSES_SWEMStopWords_part1Average, rawdata_BIOSSES_SWEMCharFiltering_part1Average, rawdata_BIOSSES_SWEMTokenizer_part1Average, rawdata_BIOSSES_SWEMLC_part1Average,rawdata_BIOSSES_SWEMStopWords_part2Average, rawdata_BIOSSES_SWEMCharFiltering_part2Average, rawdata_BIOSSES_SWEMTokenizer_part2Average, rawdata_BIOSSES_SWEMLC_part2Average,rawdata_BIOSSES_SWEMStopWords_part3Average, rawdata_BIOSSES_SWEMCharFiltering_part3Average, rawdata_BIOSSES_SWEMTokenizer_part3Average, rawdata_BIOSSES_SWEMLC_part3Average,rawdata_BIOSSES_SWEMStopWords_part1Sum, rawdata_BIOSSES_SWEMCharFiltering_part1Sum, rawdata_BIOSSES_SWEMTokenizer_part1Sum, rawdata_BIOSSES_SWEMLC_part1Sum,rawdata_BIOSSES_SWEMStopWords_part2Sum, rawdata_BIOSSES_SWEMCharFiltering_part2Sum, rawdata_BIOSSES_SWEMTokenizer_part2Sum, rawdata_BIOSSES_SWEMLC_part2Sum,rawdata_BIOSSES_SWEMStopWords_part3Sum, rawdata_BIOSSES_SWEMCharFiltering_part3Sum, rawdata_BIOSSES_SWEMTokenizer_part3Sum, rawdata_BIOSSES_SWEMLC_part3Sum)

SWEM_MedSTS <- cbind(rawdata_MedSTS_SWEMStopWords_part1Max, rawdata_MedSTS_SWEMCharFiltering_part1Max, rawdata_MedSTS_SWEMTokenizer_part1Max, rawdata_MedSTS_SWEMLC_part1Max,rawdata_MedSTS_SWEMStopWords_part2Max, rawdata_MedSTS_SWEMCharFiltering_part2Max, rawdata_MedSTS_SWEMTokenizer_part2Max, rawdata_MedSTS_SWEMLC_part2Max,rawdata_MedSTS_SWEMStopWords_part3Max, rawdata_MedSTS_SWEMCharFiltering_part3Max, rawdata_MedSTS_SWEMTokenizer_part3Max, rawdata_MedSTS_SWEMLC_part3Max,rawdata_MedSTS_SWEMStopWords_part1Min, rawdata_MedSTS_SWEMCharFiltering_part1Min, rawdata_MedSTS_SWEMTokenizer_part1Min, rawdata_MedSTS_SWEMLC_part1Min,rawdata_MedSTS_SWEMStopWords_part2Min, rawdata_MedSTS_SWEMCharFiltering_part2Min, rawdata_MedSTS_SWEMTokenizer_part2Min, rawdata_MedSTS_SWEMLC_part2Min,rawdata_MedSTS_SWEMStopWords_part3Min, rawdata_MedSTS_SWEMCharFiltering_part3Min, rawdata_MedSTS_SWEMTokenizer_part3Min, rawdata_MedSTS_SWEMLC_part3Min,rawdata_MedSTS_SWEMStopWords_part1Average, rawdata_MedSTS_SWEMCharFiltering_part1Average, rawdata_MedSTS_SWEMTokenizer_part1Average, rawdata_MedSTS_SWEMLC_part1Average,rawdata_MedSTS_SWEMStopWords_part2Average, rawdata_MedSTS_SWEMCharFiltering_part2Average, rawdata_MedSTS_SWEMTokenizer_part2Average, rawdata_MedSTS_SWEMLC_part2Average,rawdata_MedSTS_SWEMStopWords_part3Average, rawdata_MedSTS_SWEMCharFiltering_part3Average, rawdata_MedSTS_SWEMTokenizer_part3Average, rawdata_MedSTS_SWEMLC_part3Average,rawdata_MedSTS_SWEMStopWords_part1Sum, rawdata_MedSTS_SWEMCharFiltering_part1Sum, rawdata_MedSTS_SWEMTokenizer_part1Sum, rawdata_MedSTS_SWEMLC_part1Sum,rawdata_MedSTS_SWEMStopWords_part2Sum, rawdata_MedSTS_SWEMCharFiltering_part2Sum, rawdata_MedSTS_SWEMTokenizer_part2Sum, rawdata_MedSTS_SWEMLC_part2Sum,rawdata_MedSTS_SWEMStopWords_part3Sum, rawdata_MedSTS_SWEMCharFiltering_part3Sum, rawdata_MedSTS_SWEMTokenizer_part3Sum, rawdata_MedSTS_SWEMLC_part3Sum)

SWEM_CTR <- cbind(rawdata_CTR_SWEMStopWords_part1Max, rawdata_CTR_SWEMCharFiltering_part1Max, rawdata_CTR_SWEMTokenizer_part1Max, rawdata_CTR_SWEMLC_part1Max,rawdata_CTR_SWEMStopWords_part2Max, rawdata_CTR_SWEMCharFiltering_part2Max, rawdata_CTR_SWEMTokenizer_part2Max, rawdata_CTR_SWEMLC_part2Max,rawdata_CTR_SWEMStopWords_part3Max, rawdata_CTR_SWEMCharFiltering_part3Max, rawdata_CTR_SWEMTokenizer_part3Max, rawdata_CTR_SWEMLC_part3Max,rawdata_CTR_SWEMStopWords_part1Min, rawdata_CTR_SWEMCharFiltering_part1Min, rawdata_CTR_SWEMTokenizer_part1Min, rawdata_CTR_SWEMLC_part1Min,rawdata_CTR_SWEMStopWords_part2Min, rawdata_CTR_SWEMCharFiltering_part2Min, rawdata_CTR_SWEMTokenizer_part2Min, rawdata_CTR_SWEMLC_part2Min,rawdata_CTR_SWEMStopWords_part3Min, rawdata_CTR_SWEMCharFiltering_part3Min, rawdata_CTR_SWEMTokenizer_part3Min, rawdata_CTR_SWEMLC_part3Min,rawdata_CTR_SWEMStopWords_part1Average, rawdata_CTR_SWEMCharFiltering_part1Average, rawdata_CTR_SWEMTokenizer_part1Average, rawdata_CTR_SWEMLC_part1Average,rawdata_CTR_SWEMStopWords_part2Average, rawdata_CTR_SWEMCharFiltering_part2Average, rawdata_CTR_SWEMTokenizer_part2Average, rawdata_CTR_SWEMLC_part2Average,rawdata_CTR_SWEMStopWords_part3Average, rawdata_CTR_SWEMCharFiltering_part3Average, rawdata_CTR_SWEMTokenizer_part3Average, rawdata_CTR_SWEMLC_part3Average,rawdata_CTR_SWEMStopWords_part1Sum, rawdata_CTR_SWEMCharFiltering_part1Sum, rawdata_CTR_SWEMTokenizer_part1Sum, rawdata_CTR_SWEMLC_part1Sum,rawdata_CTR_SWEMStopWords_part2Sum, rawdata_CTR_SWEMCharFiltering_part2Sum, rawdata_CTR_SWEMTokenizer_part2Sum, rawdata_CTR_SWEMLC_part2Sum,rawdata_CTR_SWEMStopWords_part3Sum, rawdata_CTR_SWEMCharFiltering_part3Sum, rawdata_CTR_SWEMTokenizer_part3Sum, rawdata_CTR_SWEMLC_part3Sum)

rawdata_SWEM <- list( label = "SWEM", biosses = SWEM_BIOSSES, medsts = SWEM_MedSTS, ctr = SWEM_CTR)