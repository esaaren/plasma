# coding: utf-8

# In[1]:


get_ipython().system(u'pip install tensorflow==1.9.0')
get_ipython().system(u'pip install keras==2.1.0')


# In[27]:


import glob
import os.path as path
from scipy import misc
import os
import google.datalab.storage as storage
import pickle
import re
import nltk
from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize 
from collections import Counter


import tensorflow as tf
from tensorflow.python.platform import gfile
from tensorflow.python.saved_model import builder as saved_model_builder
from tensorflow.python.saved_model import tag_constants, signature_constants
from tensorflow.python.saved_model.signature_def_utils_impl import predict_signature_def
from tensorflow.python.lib.io import file_io

import keras
from keras import backend as K
from keras import layers, models
from keras.utils import np_utils
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.optimizers import SGD


import numpy as np
from io import BytesIO


# In[2]:


# Define the bucket location
BUCKET_PATH = 'gs://chakra-landing-data/text'
print BUCKET_PATH


# In[3]:


def get_inputs (bucket_path, num_files):
  file_list= gfile.ListDirectory(bucket_path)
  X = {}
  
  sess = tf.Session()
  with sess.as_default():
    for filename in file_list:
      file_tensor = tf.read_file(bucket_path + '/' + filename)
      file_string = sess.run(file_tensor)
      X[filename] = file_string
    
  return X


# In[ ]:


file_dict = get_inputs(BUCKET_PATH, 2187)


# In[7]:


# Save to file 
with open('text_input.pickle', 'wb') as handle:
  pickle.dump(file_dict, handle, protocol=pickle.HIGHEST_PROTOCOL)


# In[4]:


# Optional Read from File 
with open('text_input.pickle', 'rb') as handle:
  file_dict = pickle.load(handle)


# In[8]:


nltk.download('stopwords')
nltk.download('punkt')


# In[24]:


def clean_text(s):
    # Stop words dict
    stop_words = set(stopwords.words('english'))
    # Lowercase
    stripped = s.lower()
    # Replace special characters with ""
    stripped = re.sub(r"[-()\"#/@;:<>{}`+=~|.!?,]", "", stripped)
    # Change any whitespace to single space
    stripped = re.sub("\s+", " ", stripped)
    # Remove start and end whitespace
    stripped = stripped.strip()
    # Remove stop words
    word_tokens = word_tokenize(stripped)
    filtered_sentence = [w for w in word_tokens if not w in stop_words] 
    stripped = " ".join(filtered_sentence)
    return stripped


# In[25]:


# Create a dict which has the text standardized

clean_file_dict = {}

for file_key in file_dict:
  clean_file_dict[file_key] = clean_text(file_dict[file_key])  


# In[45]:


with open('clean_file_dict.pickle', 'wb') as handle:
  pickle.dump(clean_file_dict, handle, protocol=pickle.HIGHEST_PROTOCOL)


# In[ ]:


#print clean_file_dict


# In[ ]:


# Get all of the words we need 
word_list = []
for file_key in clean_file_dict:
  words = word_tokenize(clean_file_dict[file_key])
  for word in words:
    word_list.append(word)
  
print word_list


# In[30]:


# Save word list 

with open('word_list.pickle', 'wb') as handle:
  pickle.dump(word_list, handle, protocol=pickle.HIGHEST_PROTOCOL)


# In[ ]:


longer_than_one_letter = [word for word in word_list if len(word) > 1]

word_counter = Counter(longer_than_one_letter)

print len(word_counter.most_common(5000))

common_word_list = []
top_1000 = word_counter.most_common(1000)

for i in xrange(len(top_1000)):
  common_word_list.append(top_1000[i][0])
  
#print common_word_list


# In[44]:


# Save the common word list 

with open('common_word_list.pickle', 'wb') as handle:
  pickle.dump(common_word_list, handle, protocol=pickle.HIGHEST_PROTOCOL)
