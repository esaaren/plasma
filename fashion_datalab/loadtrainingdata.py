
# coding: utf-8

# In[1]:


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

import numpy as np
from io import BytesIO


# In[7]:


# Get Cleaned File Dict 
with open('clean_file_dict.pickle', 'rb') as handle:
  clean_file_dict = pickle.load(handle)


# In[3]:


# Get Common Word List 
with open('common_word_list.pickle', 'rb') as handle:
  common_word_list = pickle.load(handle)


# In[12]:


# Define the bucket location
BUCKET_PATH = 'gs://chakra-landing-data/data'


# In[ ]:


# Get the training data based on the clean file dict data
# We will use the key to get the filename and iterate over each folder, loading the images into a 64x64 array that corresponds to the value clean text
# We will then transform the value clean text into a 1D vector of length = len(common_word_list) with 1 or 0 at each index based on the common word index

# Use incrementor because we'll have multiple images for each text when we create training arrays
i = 0

# X will be the input data (Text)
# Y will be the output data (Image)

X_list = []
Y_list = []

inc = 0

sess = tf.Session()
with sess.as_default():
  for filename in clean_file_dict:
    data_folder = filename.split('_')[0]
    full_path = BUCKET_PATH + '/' + data_folder
    image_list = gfile.ListDirectory(full_path)
    
    # Only take images
    image_list = [file for file in image_list if '.jpg' in file]
    
    # Get the text data 
    image_text = clean_file_dict[filename]
    
    # Create a vector, by searching the product text in the common word list. The vector will be the length of the common word vector
    image_words = word_tokenize(image_text)
    image_word_vector = np.zeros(len(common_word_list))
    for x in xrange(len(common_word_list)):
      current_word = common_word_list[x]
      for z in xrange(len(image_words)):
        if image_words[z] == current_word:
          image_word_vector[x] = 1
          break
    
    # Now for each image in the dir, we need to convert it to a flat array 64x64 and add the inputs & outputs to the X & Y arrays
  
    for each_image in image_list:
      image = tf.read_file(os.path.join(full_path + '/', each_image))
      image = tf.image.decode_jpeg(image,channels=3)
      resized_image = tf.image.resize_images(image, [64, 64])
      image_array = sess.run(resized_image)
      image_array_flat = image_array.flatten()
      X_list.append(image_word_vector)
      Y_list.append(image_array_flat)
  
    inc = inc + 1
    
    if (inc % 100) == 0:
      print "Loaded {} images".format(inc)
      
    if inc > 1000:
      break


X = np.asarray(X_list)
Y = np.asarray(Y_list)

with open('training_input.pickle', 'wb') as handle:
  pickle.dump(X, handle, protocol=pickle.HIGHEST_PROTOCOL)
  
with open('training_output.pickle', 'wb') as handle:
  pickle.dump(Y, handle, protocol=pickle.HIGHEST_PROTOCOL)


# In[ ]:


# Build the keras model now 

model = Sequential()

input_size = len(common_word_list)
output_size = 64*64*3
hidden_size = (input_size + output_size) / 2


model.add(Dense(input_size, activation="tanh", kernel_initializer="uniform", input_dim=input_size))   # input neuron expecting dimension 1
model.add(Dense(hidden_size, activation="sigmoid", kernel_initializer="uniform"))                # hidden layer neurons
model.add(Dense(output_size, kernel_initializer="uniform"))

model.compile(loss='mse', optimizer=SGD(lr=0.1, momentum=0.99, decay=0.001, nesterov=False))

model.fit(X, Y, epochs=10, batch_size=100, validation_split=0.2, verbose=0)
