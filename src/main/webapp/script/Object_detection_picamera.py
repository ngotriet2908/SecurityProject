######## Picamera Object Detection Using Tensorflow Classifier #########
#
# Author: Evan Juras
# Date: 4/15/18
# Description: 
# This program uses a TensorFlow classifier to perform object detection.
# It loads the classifier uses it to perform object detection on a Picamera feed.
# It draws boxes and scores around the objects of interest in each frame from
# the Picamera. It also can be used with a webcam by adding "--usbcam"
# when executing this script from the terminal.

## Some of the code is copied from Google's example at
## https://github.com/tensorflow/models/blob/master/research/object_detection/object_detection_tutorial.ipynb

## and some is copied from Dat Tran's example at
## https://github.com/datitran/object_detector_app/blob/master/object_detection_app.py

## but I changed it to make it more understandable to me.


# Import packages
import os
import cv2
import numpy as np
from picamera.array import PiRGBArray
from picamera import PiCamera
import tensorflow as tf
import argparse
import sys

# Set up camera constants
# IM_WIDTH = 1280
# IM_HEIGHT = 720
IM_WIDTH = 640    
IM_HEIGHT = 480  

# Select camera type (if user enters --usbcam when calling this script,
# a USB webcam will be used)
camera_type = 'picamera'
#parser = argparse.ArgumentParser()
# parser.add_argument('--usbcam', help='Use a USB webcam instead of picamera',
#                     action='store_true')
# args = parser.parse_args()
# if args.usbcam:
#     camera_type = 'usb'
if (len(sys.argv) > 1):
    server_address = sys.argv[1]
print("server is at: " + server_address)

# This is needed since the working directory is the object_detection folder.
sys.path.append('..')

# Import utilites
from utils import label_map_util
from utils import visualization_utils as vis_util

# Name of the directory containing the object detection module we're using
MODEL_NAME = 'ssdlite_mobilenet_v2_coco_2018_05_09'

# Grab path to current working directory
CWD_PATH = os.getcwd()

# Path to frozen detection graph .pb file, which contains the model that is used
# for object detection.
PATH_TO_CKPT = os.path.join(CWD_PATH,MODEL_NAME,'frozen_inference_graph.pb')

# Path to label map file
PATH_TO_LABELS = os.path.join(CWD_PATH,'data','mscoco_label_map.pbtxt')

# Number of classes the object detector can identify
NUM_CLASSES = 90

## Load the label map.
# Label maps map indices to category names, so that when the convolution
# network predicts `5`, we know that this corresponds to `airplane`.
# Here we use internal utility functions, but anything that returns a
# dictionary mapping integers to appropriate string labels would be fine
label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
category_index = label_map_util.create_category_index(categories)

# print(label_map)
# print(categories)
# print(category_index)


# Load the Tensorflow model into memory.
detection_graph = tf.Graph()
with detection_graph.as_default():
    od_graph_def = tf.GraphDef()
    with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
        serialized_graph = fid.read()
        od_graph_def.ParseFromString(serialized_graph)
        tf.import_graph_def(od_graph_def, name='')

    sess = tf.Session(graph=detection_graph)


# Define input and output tensors (i.e. data) for the object detection classifier

# Input tensor is the image
image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')

# Output tensors are the detection boxes, scores, and classes
# Each box represents a part of the image where a particular object was detected
detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')

# Each score represents level of confidence for each of the objects.
# The score is shown on the result image, together with the class label.
detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')

# Number of objects detected
num_detections = detection_graph.get_tensor_by_name('num_detections:0')

# Initialize frame rate calculation
frame_rate_calc = 1
freq = cv2.getTickFrequency()
font = cv2.FONT_HERSHEY_SIMPLEX

# Initialize camera and perform object detection.
# The camera has to be set up and used differently depending on if it's a
# Picamera or USB webcam.

# I know this is ugly, but I basically copy+pasted the code for the object
# detection loop twice, and made one work for Picamera and the other work
# for USB.

pause = 0
status_in_frame = False
in_frame_counter = 0
out_frame_counter = 0
frame_count = 0

def countAndSend(scores, classes, frame):
    global pause, in_frame_counter, out_frame_counter, status_in_frame, frame_count

    in_frame = False
    for i in range(0, len(classes)): 
        if (scores[i] > 0 and classes[i] == 1):
            in_frame = True
            break
        elif (scores[i] <= 0):
            break 
    if (in_frame == True and status_in_frame == False):
        in_frame_counter = in_frame_counter + 1
        out_frame_counter = 0
    elif (in_frame == False and status_in_frame == True):
        out_frame_counter = out_frame_counter + 1
        in_frame_counter = 0

    if (in_frame_counter > 10 and status_in_frame == False):
        print("people in 10 frame")
        update_status(1,"people in 10 frame")
        status_in_frame = True
    if (out_frame_counter > 10 and status_in_frame == True):
        print("people out 10 frame")
        update_status(1,"people out 10 frame")
        status_in_frame = False

    if (frame_count >= 2):
        send_log_image(frame, in_frame)
        frame_count = 0
    else:
        frame_count = frame_count + 1

import requests

def send_log_image(frame, status):
    import requests
    
    _, img_encoded = cv2.imencode('.jpg', frame)
    url = server_address + "rest/log/"
    if (status == True):
        files = {
            'picture': img_encoded,
            'status': "People are in picture",
            'room_id':1
        }
    else:
        files = {
            'picture': img_encoded,
            'status': "People are not in picture",
            'room_id':1 
        }
    response = requests.post(url, files=files)

def send_latest_image(frame, room_id):
    import requests
    
    _, img_encoded = cv2.imencode('.jpg', frame)
    url = server_address + "rest/room/updatepicture/" + str(room_id)
    files = {
        'picture': img_encoded,
    }
    response = requests.post(url, files=files)

def update_status(room_id, people_in_frame):
    import requests
    
    url = server_address + "rest/room/status"
    if (people_in_frame == True):
        files = {
            'status': "People are in picture",
            'room_id':room_id
        }
    else:
        files = {
            'status': "People are not in picture",
            'room_id':room_id
        }
    response = requests.post(url, files=files)



### Picamera ###
if camera_type == 'picamera':
    # Initialize Picamera and grab reference to the raw capture
    camera = PiCamera()
    camera.resolution = (IM_WIDTH,IM_HEIGHT)
    camera.framerate = 10
    rawCapture = PiRGBArray(camera, size=(IM_WIDTH,IM_HEIGHT))
    rawCapture.truncate(0)

    for frame1 in camera.capture_continuous(rawCapture, format="bgr",use_video_port=True):

        t1 = cv2.getTickCount()
        
        # Acquire frame and expand frame dimensions to have shape: [1, None, None, 3]
        # i.e. a single-column array, where each item in the column has the pixel RGB value
        frame = np.copy(frame1.array)
        frame.setflags(write=1)
        frame_expanded = np.expand_dims(frame, axis=0)

        # Perform the actual detection by running the model with the image as input
        (boxes, scores, classes, num) = sess.run(
            [detection_boxes, detection_scores, detection_classes, num_detections],
            feed_dict={image_tensor: frame_expanded})
        

        # print(scores)
        # print(classes)
        # print("-------------------------------")

        # Draw the results of the detection (aka 'visulaize the results')
        vis_util.visualize_boxes_and_labels_on_image_array(
            frame,
            np.squeeze(boxes),
            np.squeeze(classes).astype(np.int32),
            np.squeeze(scores),
            category_index,
            use_normalized_coordinates=True,
            line_thickness=8,
            min_score_thresh=0.40)


        cv2.putText(frame,"FPS: {0:.2f}".format(frame_rate_calc),(30,50),font,1,(255,255,0),2,cv2.LINE_AA)
        
        countAndSend(np.squeeze(scores), np.squeeze(classes).astype(np.int32), frame)

        # All the results have been drawn on the frame, so it's time to display it.
        cv2.imshow('Object detector', frame)

        t2 = cv2.getTickCount()
        time1 = (t2-t1)/freq
        frame_rate_calc = 1/time1

        # Press 'q' to quit
        if cv2.waitKey(1) == ord('q'):
            break

        rawCapture.truncate(0)

    camera.close()

cv2.destroyAllWindows()

