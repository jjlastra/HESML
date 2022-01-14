import sys

from sentence_transformers import SentenceTransformer

# get the input parameters

# get the model path

strModelPath = sys.argv[1]
print("MODEL " + strModelPath)

# get the input sentences and the output embedding paths.

absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

# strModelPath = "seiya/oubiobert-base-uncased"
# absPathTempSentencesFile = "../wordpiecetokenizer_lc_nonestopwords_none_none_pubmedbert-base-uncased-abstract__Sents.txt"
# absPathTempVectorsFile = "../wordpiecetokenizer_lc_nonestopwords_none_none_pubmedbert-base-uncased-abstract__Vecs.txt"

model = SentenceTransformer(strModelPath, device="cpu")
model.save("downl_" + strModelPath.replace("/", "_"))

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if (not row == "") and (len(row)>1):
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip()
            s2 = data[1].strip()

            # infer the vectors of the sentences

            sentence_embeddings_1 = model.encode(s1, convert_to_numpy=True)
            sentence_embeddings_2 = model.encode(s2, convert_to_numpy=True)

            sentence_embeddings_1 = sentence_embeddings_1.tolist()  # nested lists with same data, indices
            sentence_embeddings_2 = sentence_embeddings_2.tolist()  # nested lists with same data, indices

            # format and write the output

            strVector1 = ",".join(map(str, sentence_embeddings_1))
            strVector2 = ",".join(map(str, sentence_embeddings_2))

            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)
