from flask import Flask, render_template, request
import string
import numpy as np
from nltk.tokenize import word_tokenize
import gensim.downloader as api
import tensorflow as tf


app = Flask(__name__)

#import Word2Vec embeddings
word_vectors = api.load("word2vec-google-news-300")

#PLEASE CHANGE THIS TO YOUR PATH TO hero_model.tflite
model = "Project Artefact\Hero Model V1\hero_model.tflite"
inferencing = tf.lite.Interpreter(model_path=model)
inferencing.allocate_tensors()

#recieving the input and outputs of the model
input = inferencing.get_input_details()
output = inferencing.get_output_details()

#we use the same preprocessing as the model in notebook
def preprocess_text(text, max_seq_length):
    
    #lowercasing input
    text = text.lower()
    #removing invalid characters
    text = text.translate(str.maketrans('', '', string.punctuation + string.digits))

    #creating tokens for input
    input_tokens = word_tokenize(text)

    #creating a place holder for the embedded tokens
    embeddings = []

    #embedding tokens using Word2Vec
    for token in input_tokens:
        if token in word_vectors.key_to_index:
            embeddings.append(word_vectors.get_vector(token))
        else:
            #have a zero if it isnt present in the list
            embeddings.append(np.zeros(word_vectors.vector_size))

    #we need embeddings as numpy array for the model to read it
    numpy_array = np.array(embeddings)

    #the input text needs to be the correct dimension no matter the length
    embedded_message = numpy_array.shape[0]
    #when the input message is smaller than the required length
    if embedded_message < max_seq_length:
        #pad it with zero keeping the shape of the array still
        padding_shape = ((0, max_seq_length - embedded_message), (0, 0))
        numpy_array = np.pad(numpy_array, padding_shape, mode='constant')
    #when its bigger than the required length    
    elif embedded_message > max_seq_length:
        #condense it into the max_seq_length
        numpy_array = numpy_array[:max_seq_length]
    return numpy_array
#max length = 1 because of required shape (1, 300)
max_seq_length = 1

#label mappings
class_to_category = {
    0: 'Neurological Disorders',
    1: 'Genetic Disorders',
    2: 'Gastrointestinal',
    3: 'Hematological Disorders',
    4: 'Autoimmune Disorders',
    5: 'Endocrine Disorders',
    6: 'Cancer',
    7: 'Musculoskeletal Disorders',
    8: 'Respiratory Disorders',
    9: 'Cardiovascular Disorders',
    10: 'Renal Disorders',
    11: 'Psychiatric Disorders',
}

@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == 'POST':
        #receive input text
        user_input = request.form['user_input']

        #use preprocessing
        preprocessor = preprocess_text(user_input, max_seq_length)
        print("shape of input:", preprocessor.shape)

        #the model needs the dtype of input to be float32
        preprocessor = preprocessor.astype(np.float32)

        #double check the shape model needs = (1, 300)
        model_shape = input[0]['shape']
        print("model shape:", model_shape)

        #reshape input to the model shape
        preprocessor = preprocessor.reshape(model_shape)
        print("Shape after reshaping:", preprocessor.shape)

        #input tensor for model inference
        inferencing.set_tensor(input[0]['index'], preprocessor)

        #inferencing user input
        inferencing.invoke()

        #output tensor from the model
        output_tensor = inferencing.get_tensor(output[0]['index'])

        #create the predicted class from model inferencing
        predicted_class = np.argmax(output_tensor)

        #respond to the user...
        category_name = class_to_category.get(predicted_class, 'Unknown')
        result = f"Entered: {user_input}, Possible condition: {category_name}"
        return render_template('index.html', result=result)
    return render_template('index.html', result=None)

if __name__ == '__main__':
    app.run(debug=True)
