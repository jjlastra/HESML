# Import the dependencies

import tensorflow as tf
import tensorflow_hub as hub
import logging
import sys

# get the arguments from the input

module_url = sys.argv[1] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempSentencesFile = sys.argv[2] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[3] # the output path

# set up the logger info

logger = tf.get_logger()
logger.setLevel(logging.FATAL)

# Load the model from the URL

# module_url = "https://tfhub.dev/google/universal-sentence-encoder/4"
model = hub.load(module_url)

print ("module %s loaded" % module_url)

# function for extracting the message_embeddings

def embed(input):
  return model(input)

# open the file sentences.txt

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

# iterate the sentences and get the embeddings

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if not row == "":
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip()
            s2 = data[1].strip()

            # infer the vectors of the sentences

            messages = [s1, s2]

            message_embeddings = embed(messages)

            v1 = message_embeddings[0].numpy()
            v2 = message_embeddings[1].numpy()

            # format and write the output

            strVector1 = ",".join(map(str, v1))
            strVector2 = ",".join(map(str, v2))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)


print("SCRIPTOK")
