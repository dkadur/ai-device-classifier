from controller import Robot, Camera
from PIL import Image
import numpy as np
import subprocess
import requests
import time
import os
import json

TIME_STEP = 64
CAPTURE_INTERVAL = 3  # in seconds
BOOLEAN = True
id_list=[]

# create the Robot instance
robot = Robot()

# enable the camera (assuming you have a camera in your world)
camera = robot.getDevice('CAM')
camera.enable(TIME_STEP)
camera.recognitionEnable(TIME_STEP)

#Put wheels into list
wheels = []
for i in range(4):
    wheels.append(robot.getDevice('wheel'+str(i+1)))

# Initialize the time counter
last_capture_time = 0

def takeImage():
    timestamp = int(time.time())
    save_directory = r"C:\Users\Deepak\Webots Projects\DeviceDetection\images"
    image_filename = f"image_{timestamp}.jpg"
    image_path = os.path.join(save_directory, image_filename)
    camera.saveImage(image_path, 50)
    url = 'http://127.0.0.1:5000/store_image'
    
    result = subprocess.run(
        [r"C:\Users\Deepak\anaconda3\envs\ml\python.exe", r"C:\Users\Deepak\Python\ml\tflite_test.py", image_path],
        stdout=subprocess.PIPE,
        text=True,
    )
    output_from_subprocess = json.loads(result.stdout.strip())
    device_name = output_from_subprocess.get("device_name", "Unknown")
    
    data = {'image_path': image_path, "classification": device_name}
    response = requests.post(url, json=data)
    if response.status_code == 200:
        print("Image path sent successfully.")
    else:
        print("Failed to send the image path.")

 
while robot.step(TIME_STEP) != -1:
    # Get the current simulation time
    current_time = robot.getTime()
    
    for wheel in wheels:
            wheel.setPosition(float("inf"))
            wheel.setVelocity(5)
            
    objects = camera.getRecognitionObjects()

    # Loop through the detected objects
    for obj in objects:
        label = obj.getModel()  # Get the label of the detected object
        size_x, size_y = obj.getSizeOnImage() #Get size of bounded box
        id = obj.getId()
        if size_x >= 750 and size_y >= 750 and id not in id_list:
            takeImage()
            id_list.append(id)
    
    # Check if it's time to capture an image
    
    #if current_time - last_capture_time >= CAPTURE_INTERVAL and BOOLEAN:
        # Capture the camera image from the robot's perspective
        
        #image = camera.getImage()
        #camera.saveImage(r"C:\Users\Deepak\Webots Projects\DeviceDetection\image.jpg", 50)
        #subprocess.run([r"C:\Users\Deepak\anaconda3\envs\ml\python.exe", r"C:\Users\Deepak\Python\ml\tflite_test.py"])
        #BOOLEAN = False

    # Your robot control logic here

    pass