import sys
from bert import tokenization

# Get the arguments

m_vocab_file = sys.argv[1]
sentence = sys.argv[2]

m_do_lower_case = True

# Tokenize and print the tokenized sentence

tokenizer = tokenization.FullTokenizer(vocab_file=m_vocab_file, do_lower_case=m_do_lower_case)

tokens_sentence_1 = tokenizer.tokenize(sentence)

# join the tokens into a new preprocessed sentence

new_line_sentence_1 = " ".join(tokens_sentence_1)

# print the results

print(new_line_sentence_1.rstrip())
