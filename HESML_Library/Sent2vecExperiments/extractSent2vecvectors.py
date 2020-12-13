import sent2vec
import sys

# import the arguments

strModelPath = sys.argv[1]
absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

# strModelPath = "BioSentVec_PubMed_MIMICIII-bigram_d700.bin"
# absPathTempSentencesFile = "tempSentences.txt"
# absPathTempVectorsFile = "tempVecs.txt"

# initialize the sent2vec model

model = sent2vec.Sent2vecModel()

#load the model

model.load_model(strModelPath)

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

            # infer the vectors of the sentences

            messages = [s1, s2]

            message_embeddings = model.embed_sentences(messages)

            v1 = message_embeddings[0]
            v2 = message_embeddings[1]

            # format and write the output

            strVector1 = ",".join(map(str, v1))
            strVector2 = ",".join(map(str, v2))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)

# release the model from memory

# model.release_shared_mem()

print("SCRIPTOK")
