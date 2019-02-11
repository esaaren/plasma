# coding: utf-8

# In[2]:


import glob
import os.path as path
from scipy import misc
import os
import google.datalab.storage as storage
import pickle
import re
import nltk
from PIL import Image
import matplotlib.pyplot as plt
from nltk.corpus import stopwords 
from nltk.tokenize import word_tokenize 
from collections import Counter


import tensorflow as tf
from tensorflow.python.platform import gfile
from tensorflow.python.saved_model import builder as saved_model_builder
from tensorflow.python.saved_model import tag_constants, signature_constants
from tensorflow.python.saved_model.signature_def_utils_impl import predict_signature_def
from tensorflow.python.lib.io import file_io
from tensorflow.keras.preprocessing.image import load_img, array_to_img


import keras
from keras import backend as K
from keras import layers, models
from keras.utils import np_utils
from keras.models import Sequential
from keras.layers.core import Dense, Activation
from keras.optimizers import SGD

import numpy as np
from io import BytesIO


# In[3]:


# Get Cleaned File Dict 
with open('clean_file_dict.pickle', 'rb') as handle:
  clean_file_dict = pickle.load(handle)


# In[4]:


# Get Common Word List 
with open('common_word_list.pickle', 'rb') as handle:
  common_word_list = pickle.load(handle)


# In[5]:


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
      # Important to divide by 255 when plotting for matplot lib because the tf image is 0-->255, this will also normalize data from 0 --> 1
      image = tf.cast(image, tf.float32) / 255.0
      resized_image = tf.image.resize_images(image, [88, 59])
      image_array = sess.run(resized_image)
      image_array_flat = image_array.flatten()
      
      
      X_list.append(image_word_vector)
      Y_list.append(image_array_flat)
  
    if (inc % 100) == 0:
      print "Loaded {} images".format(inc)
      
    if inc > 100:
      break

    inc = inc + 1


X = np.asarray(X_list)
Y = np.asarray(Y_list)

with open('training_input.pickle', 'wb') as handle:
  pickle.dump(X, handle, protocol=pickle.HIGHEST_PROTOCOL)
  
with open('training_output.pickle', 'wb') as handle:
  pickle.dump(Y, handle, protocol=pickle.HIGHEST_PROTOCOL)


# In[6]:


# Optional to read in the inputs from before

# Get labelled input  
with open('training_input.pickle', 'rb') as handle:
  X = pickle.load(handle)
  
  # Get labelled output
with open('training_output.pickle', 'rb') as handle:
  Y = pickle.load(handle)


# In[7]:


print X.shape
print Y.shape


# In[16]:


# Build the keras model now 

model = Sequential()

input_size = len(common_word_list)
output_size = 88*59*3
hidden_size = (input_size + output_size) / 2


model.add(Dense(input_size, activation="relu", input_dim=input_size))   # input neuron expecting dimension 1
model.add(Dense(hidden_size, activation="relu"))                # hidden layer neurons
model.add(Dense(output_size, activation="sigmoid"))

model.compile(loss='mse', optimizer=SGD(lr=0.9, momentum=0.99, decay=0.001, nesterov=False))

model.fit(X, Y, epochs=150, batch_size=10, validation_split=0.2, verbose=0)


# In[13]:


X_predict = np.zeros([1,636])
X_predict[0] = X[0]
prediction = model.predict(X_predict)


# In[14]:


predict_array = prediction[0].reshape(88,59,3)
print predict_array
print Y[0]

plt.imshow(predict_array)

