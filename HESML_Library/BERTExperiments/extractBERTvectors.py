import sys
from bert_serving.client import BertClient
from bert_serving.server.helper import get_args_parser
from bert_serving.server import BertServer
import tensorflow as tf
import logging
logger = tf.get_logger()
logger.setLevel(logging.ERROR)

# get the input parameters

# get the pooling strategy an layers

strPoolingStrategy = sys.argv[1]
strPoolingLayer = sys.argv[2]

# get the model path

strModelPath = sys.argv[3]

# get the input sentences and the output embedding paths.

absPathTempSentencesFile = sys.argv[4] # the preprocessed sentences path in format: s1 \t s2 \n
absPathTempVectorsFile = sys.argv[5] # the output path

# get the server port

pythonServerPort = sys.argv[6]
checkPointFilename = sys.argv[7]
FineTunedModelPath = sys.argv[8]

# strPoolingStrategy = "REDUCE_MEAN"
# strPoolingLayer = "-2"
# strModelPath = "../BERTExperiments/BERTPretrainedModels/biobert_v1.0_pubmed_pmc"
# absPathTempSentencesFile = "../BERTExperiments/tempSentences.txt"
# absPathTempVectorsFile = "../BERTExperiments/tempVecs.txt"
# pythonServerPort = "5555"
# checkPointFilename = "model.ckpt-150000.index"
# FineTunedModelPath = "../BERTExperiments/BERTPretrainedModels/Bio+ClinicalBERT"

# The pooling layer is modified from "-2,-1" to "-2 -1"

strPoolingLayer = " ".join(strPoolingLayer.split(","))

# Set the python input and output server ports

pythonServerPort_ = int(pythonServerPort)
port_out_ = pythonServerPort_+1

## INIT THE SERVER CODE ##

# args = get_args_parser().parse_args([
#                                  '-pooling_strategy', strPoolingStrategy,
#                                  '-pooling_layer', strPoolingLayer,
#                                  '-model_dir', strModelPath,
#                                  '-port', str(pythonServerPort_),
#                                  '-port_out', str(port_out_),
#                                  '-num_worker', '5',
#                                  '-cpu'])

if checkPointFilename == "bert_model.ckpt":

    # NOT Fine tunned model

    args = get_args_parser().parse_args([
                                     '-pooling_strategy', strPoolingStrategy,
                                     '-pooling_layer', strPoolingLayer,
                                     '-device_map', "0",
                                     '-ckpt_name', checkPointFilename,
                                     '-model_dir', strModelPath,
                                     '-port', str(pythonServerPort_),
                                     '-port_out', str(port_out_)])
else:

    # Fine tunned model

    args = get_args_parser().parse_args([
        '-pooling_strategy', strPoolingStrategy,
        '-pooling_layer', strPoolingLayer,
        '-device_map', "0",
        '-ckpt_name', checkPointFilename,
        '-model_dir', strModelPath,
        '-tuned_model_dir', FineTunedModelPath,
        '-port', str(pythonServerPort_),
        '-port_out', str(port_out_)])

server = BertServer(args)
server.start()

## INIT THE CLIENT CODE ##

bc = BertClient(port=pythonServerPort_, ip='localhost', port_out=port_out_)

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