"""
Uses Keras to train model for handwritten digit detection by using MNIST data.
Does not work with Keras 2.x or above.
Requires Tensorflow backed.

Image classification heavily inspired by
https://github.com/the-laughing-monkey/learning-ai-if-you-suck-at-math
"""

from keras.models import Sequential
from keras.layers import Dense, Flatten
from keras.layers import Convolution2D, MaxPooling2D
from keras.utils.np_utils import to_categorical
from keras.datasets import mnist
import numpy

##TODO: CHECK LeNet-5 for hyperparameters

# fix random seed for reproducibility
seed = 7
numpy.random.seed(seed)
# There are 10 different digits. 0-9
nb_classes = 10
# MNIST images are 28x28
img_rows, img_cols = 28, 28
# umber of convolutional filters to use.
# No idea what should be chose here. 28 seems to work quite fine
nb_filters = 28
# size of pooling area for max pooling
pool_size = (2, 2)
# convolution kernel size
kernel_size = (3, 3)

(X_train, Y_train), (X_test, Y_test) = mnist.load_data()

# Save some training data to a file so we can show it in the Java app
X_train[0:100].tofile("mnist.csv", sep=",")

# Since the model will be used in a Java app where there are no
# grayscale pixels, we convert the training and test data to
# black-and-white also. The values are chosen based on the recommendations
# in https://deeplearning4j.org/rbm-mnist-tutorial.html
X_train[X_train > 35] = 255
X_train[X_train <= 35] = 0
X_test[X_test > 35] = 255
X_test[X_test <= 35] = 0

# Reshape the data to be used with Tensorflow.
X_train = X_train.reshape(X_train.shape[0], img_rows, img_cols, 1)
X_test = X_test.reshape(X_test.shape[0], img_rows, img_cols, 1)
input_shape = (img_rows, img_cols, 1)
Y_binary = to_categorical(Y_train, nb_classes)
Y_binary_test = to_categorical(Y_test, nb_classes)

print('X_train shape:', X_train.shape)
print(X_train.shape[0], 'train samples')
print(X_test.shape[0], 'test samples')

model = Sequential()
model.add(Convolution2D(nb_filters,
                        kernel_size[0],
                        kernel_size[1],
                        border_mode='valid',
                        input_shape=input_shape,
                        activation='sigmoid'))
model.add(MaxPooling2D(pool_size=pool_size))
model.add(Flatten())
model.add(Dense(nb_classes, activation='softmax'))

# Compile model
model.compile(loss='categorical_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])
# Fit the model
model.fit(X_train, Y_binary, nb_epoch=20, batch_size=32, verbose=2)
# Save the model to the current directory
model.save("mnist.h5f")
# evaluate the model. If verbose, cannot be run in Jupyter notebook
scores = model.evaluate(X_test, Y_binary_test, verbose=0)
print("%s: %.2f%%" % (model.metrics_names[1], scores[1]*100))
