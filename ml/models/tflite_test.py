import cv2
import tensorflow.lite as tflite
import numpy as np
import json
import sys  # Import the sys module to access command-line arguments

# Check if the image file path is provided as a command-line argument
if len(sys.argv) < 2:
    print("Usage: python tflite_test.py <image_path>")
    sys.exit(1)

# Extract the image file path from the command-line arguments
image_path = sys.argv[1]

interpreter = tflite.Interpreter(model_path=r'C:\Users\Deepak\Downloads\models_model-5771989644103122944_tflite_2023-07-15T17_02_10.894008Z_model.tflite')
interpreter.allocate_tensors()

input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()

img = cv2.imread(image_path)
img = cv2.resize(img, (224, 224))

# Preprocess the image to the required size and cast
input_shape = input_details[0]['shape']
input_tensor = np.array(np.expand_dims(img, 0))

input_index = interpreter.get_input_details()[0]["index"]
interpreter.set_tensor(input_index, input_tensor)
interpreter.invoke()
output_details = interpreter.get_output_details()

output_data = interpreter.get_tensor(output_details[0]['index'])
pred = np.squeeze(output_data)

class_ind = {
    0: "iPhone 6",
    1: "iPhone 8",
    2: "iPhone X",
    3: "iPhone 13"
}

highest_pred_loc = np.argmax(pred)

device_name = class_ind[highest_pred_loc]
#print(pred)
print(json.dumps({"device_name": device_name}))