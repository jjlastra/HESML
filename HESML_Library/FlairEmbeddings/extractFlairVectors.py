from flair.data import Sentence
from flair.embeddings import FlairEmbeddings, DocumentPoolEmbeddings
import sys

# import the arguments

strModelsPath = sys.argv[1]
absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

# examples

# strModelsPath = './embeddings/pubmed-forward.pt,./embeddings/pubmed-backward.pt'
# absPathTempSentencesFile = "tempSentences.txt"
# absPathTempVectorsFile = "tempVecs.txt"

# add the models to a list

embeddingModels = []
models = strModelsPath.split(",")
for model_path in models:
    embeddingModels.append(FlairEmbeddings(model_path))

# Create the stacked embedding model

document_embeddings = DocumentPoolEmbeddings(embeddings=embeddingModels)

# open the sentences file

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

# iterate the sentences and get the embeddings.

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if not row == "":
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip()
            s2 = data[1].strip()

            # create a sentences

            sentence1 = Sentence(s1)
            sentence2 = Sentence(s2)

            # embed words in sentence #

            document_embeddings.embed(sentence1)
            document_embeddings.embed(sentence2)

            # format and write the output

            strVector1 = ",".join(map(str, sentence1.embedding.tolist()))
            strVector2 = ",".join(map(str, sentence2.embedding.tolist()))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)

