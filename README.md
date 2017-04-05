# Digit Recognizer

The goal of this project is to recognize handwritten digits. It uses deep learning to train a handwritten digit recognizer model by using [MNIST](http://yann.lecun.com/exdb/mnist/) data as the training data. The model is built with [Keras](https://keras.io/) and [Tensorflow](https://www.tensorflow.org/). The accompanying Java app can be used to manually test the model. It uses [Deeplearning4J](https://deeplearning4j.org/) to read the Keras model and enables the user to draw a digit that is then used as an input for the model.

## Running the Java app

### Requirements
* Java 1.8 or newer
* Maven 3.2.2 or newer

### Building and running

Clone the repo
```
git clone https://github.com/Miksu82/DigitRecognizer
```

Build and run the Java app
```
cd app
maven clean package
java -jar target/digitdetector-1.0-SNAPSHOT.jar
```

## Modifying the model

### Requirements

This is my setup that I have tested with. Older versions may also work.
* Java 1.8
* Python 2.7
* Keras 1.2.2
* Tensorflow 1.0.1
* Numpy 1.12.0


### Retraining the model

The repository contains prebuilt model saved to
```
trainer/mnist.h5f
```

The current accuracy is around 97 %. If you want to you tweak the parameters and retrain the model you can do that by modifying
```
trainer/mnist-trainer.py
```

and while in *trainer* directory run
```
python mnist-trainer.py
```

The model will always be saved to your working directory so if you run the script from somewhere else the Java app can not find the new model.

## Note

This is *not* meant to be state-of-art (or even close) digit recognizer. The project goal was to learn a bit about deep learning and see how the most used deep learning frameworks are used.

For more information read the related [blog post](https://medium.com/@mikaristimki/my-first-steps-in-deep-learning-recognising-handwritten-digits-f72e72936ea4)

