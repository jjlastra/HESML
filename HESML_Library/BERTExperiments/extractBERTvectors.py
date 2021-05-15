import sys
from bert_serving.client import BertClient
from bert_serving.server.helper import get_args_parser
from bert_serving.server import BertServer
import tensorflow as tf
import logging
import socket
logger = tf.get_logger()
logger.setLevel(logging.ERROR)

# print("Num GPUs Available: ", len(tf.config.list_physical_devices('GPU')))

# Set the fine_tunned option to false by default

fine_tunned = False

# Define a function for checking the used port

def is_port_in_use(port):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        return s.connect_ex(('localhost', port)) == 0

# get the input parameters

# get the pooling strategy an layers

print(sys.argv)

strPoolingStrategy = sys.argv[1]
strPoolingLayer = sys.argv[2]

# get the model path

strModelPath = sys.argv[3]

# get the input sentences and the output embedding paths.

absPathTempSentencesFile = sys.argv[4] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[5] # the output path

# if we have a fine tunned model, get the model

if len(sys.argv) > 6:
    checkPointFilename = sys.argv[7]
    FineTunedModelPath = sys.argv[8]
    fine_tunned = True

# strPoolingStrategy = "REDUCE_MEAN"
# strPoolingLayer = "-2"
# # strModelPath = "../BERTExperiments/BERTPretrainedModels/oubiobert-base-uncased"
# strModelPath = "../BERTExperiments/BERTPretrainedModels/biobert_v1.0_pmc"
# absPathTempSentencesFile = "../BERTExperiments/wordpiecetokenizer_lc_nonestopwords_none_none_oubiobert-base-uncased__Sents.txt"
# absPathTempVectorsFile = "../BERTExperiments/tempVecs.txt"
# pythonServerPort = "5555"
# checkPointFilename = "model.ckpt-150000.index"
# # FineTunedModelPath = "../BERTExperiments/BERTPretrainedModels/oubiobert-base-uncased"
# FineTunedModelPath = "../BERTExperiments/BERTPretrainedModels/biobert_v1.0_pmc"

# The pooling layer is modified from "-2,-1" to "-2 -1"

strPoolingLayer = " ".join(strPoolingLayer.split(","))

# set the bert port
port = 5555

if is_port_in_use(port):
    # exception if the port is in use
    raise Exception("ERROR : PORT " + str(port) + " ALREADY IN USE!!!")

## INIT THE SERVER CODE ##

if not fine_tunned:

    # NOT Fine tunned model

    args = get_args_parser().parse_args([
                                     '-pooling_strategy', strPoolingStrategy,
                                     '-pooling_layer', strPoolingLayer,
                                     '-model_dir', strModelPath,
                                     '-port', str(port),
                                     '-port_out', str(port+1),
                                     '-device_map', "0"])
else:

    # Fine tunned model

    args = get_args_parser().parse_args([
        '-pooling_strategy', strPoolingStrategy,
        '-pooling_layer', strPoolingLayer,
        '-device_map', "0",
        '-ckpt_name', checkPointFilename,
        '-model_dir', strModelPath,
        '-tuned_model_dir', FineTunedModelPath,
        '-port', str(port),
        '-port_out', str(port+1)])

server = BertServer(args)
server.start()

## INIT THE CLIENT CODE ##

bc = BertClient(port=port, ip='localhost', port_out=port+1)

f = open(absPathTempSentencesFile, "r")
file = f.read().split("\n")

with open(absPathTempVectorsFile, 'w') as f:
    for row in file:
        if (not row == "") and (len(row)>1):
            data = row.split("\t")

            # get the sentences

            s1 = data[0].strip().split(" ")
            s2 = data[1].strip().split(" ")

            # infer the vectors of the sentences

            doc_vecs = bc.encode([s1, s2], is_tokenized=True)
            v1 = doc_vecs[0]
            v2 = doc_vecs[1]

            a = v1.tolist()  # nested lists with same data, indices
            b = v2.tolist()  # nested lists with same data, indices

            # format and write the output

            strVector1 = ",".join(map(str, a))
            strVector2 = ",".join(map(str, b))
            line = strVector1 + "\t" + strVector2
            f.write("%s\n" % line)

print("Server status before:")
print(bc.server_status)

bc.close()

server.close()

print("SCRIPTOK")