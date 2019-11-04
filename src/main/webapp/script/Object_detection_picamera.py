import os
import cv2
import numpy as np
from picamera.array import PiRGBArray
from picamera import PiCamera
import tensorflow as tf
import argparse
import sys
import requests
from temp import read_temp
from utils import label_map_util
from utils import visualization_utils as vis_util


# -------------------------------------------------
# Pre-defined model = ssdlite_mobilenet_v2_coco_2018_05_09, cv2

IMG_WIDTH = 640    
IMG_HEIGHT = 480  
ROOM_ID = 0
if (len(sys.argv) > 1):
    server_address = sys.argv[1]
    ROOM_ID = int(sys.argv[2])
print("server is at: " + server_address)
print("This is room: " + str(ROOM_ID))

sys.path.append('..')

MODEL_NAME = 'ssdlite_mobilenet_v2_coco_2018_05_09'
#MODEL_NAME = 'inference_graph'

CWD_PATH = os.getcwd()
PATH_TO_CKPT = os.path.join(CWD_PATH,MODEL_NAME,'frozen_inference_graph.pb')
PATH_TO_LABELS = os.path.join(CWD_PATH,'data','mscoco_label_map.pbtxt')
NUM_CLASSES = 90
label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
category_index = label_map_util.create_category_index(categories)
detection_graph = tf.Graph()
with detection_graph.as_default():
    od_graph_def = tf.GraphDef()
    with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
        serialized_graph = fid.read()
        od_graph_def.ParseFromString(serialized_graph)
        tf.import_graph_def(od_graph_def, name='')

    sess = tf.Session(graph=detection_graph)

image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')
detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')
detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')
num_detections = detection_graph.get_tensor_by_name('num_detections:0')
freq = cv2.getTickFrequency()
font = cv2.FONT_HERSHEY_SIMPLEX
pause = 0
status_in_frame = False
in_frame_counter = 0
out_frame_counter = 0
frame_count = 0
frame_temp_count = 0
# -------------------------------------------------

def countAndSend(scores, classes, frame):
    global pause, in_frame_counter, out_frame_counter, status_in_frame, frame_count,  frame_temp_count

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

    if (in_frame_counter > 7 and status_in_frame == False):
        print("people in 7 frame")
        update_status(ROOM_ID, True)
        status_in_frame = True
    if (out_frame_counter > 7 and status_in_frame == True):
        print("people out 7 frame")
        update_status(ROOM_ID , False)
        status_in_frame = False

        
    if (frame_temp_count >= 5):
        update_temp(ROOM_ID)
        frame_temp_count = 0
    else:
        frame_temp_count = frame_temp_count + 1

    if (frame_count >= 2):
        send_log_image(frame, in_frame)
        frame_count = 0
    else:
        frame_count = frame_count + 1


def send_log_image(frame, status):
    
    _, img_encoded = cv2.imencode('.jpg', frame)
    url = server_address + "rest/log/"
    if (status == True):
        files = {
            'picture': img_encoded,
            'status': "True",
            'room_id': ROOM_ID
        }
    else:
        files = {
            'picture': img_encoded,
            'status': "False",
            'room_id': ROOM_ID
        }
    response = requests.post(url, files=files)

def send_latest_image(frame, room_id):
    
    _, img_encoded = cv2.imencode('.jpg', frame)
    url = server_address + "rest/room/updatepicture/" + str(room_id)
    files = {
        'picture': img_encoded,
    }
    response = requests.post(url, files=files)

def update_status(room_id, people_in_frame):
    
    url = server_address + "rest/room/status"
    if (people_in_frame == True):
        files = {
            'people': 'True',
            'room_id':room_id
            
        }
    else:
        files = {
            'people': 'False',
            'room_id':room_id
        }
    response = requests.post(url, files=files)

def update_temp(room_id):
    tempp = str(read_temp())
    print("updating temp:" + tempp)
    url = server_address + "rest/room/temp"
    files = {
        'temp': tempp,
        'room_id':room_id
            
    }
    
    response = requests.post(url, files=files)


# -------------------------------------------------


camera = PiCamera()
camera.resolution = (IMG_WIDTH,IMG_HEIGHT)
camera.framerate = 10
rawCapture = PiRGBArray(camera, size=(IMG_WIDTH,IMG_HEIGHT))
rawCapture.truncate(0)

for frame1 in camera.capture_continuous(rawCapture, format="bgr",use_video_port=True):
    t1 = cv2.getTickCount()
    frame = np.copy(frame1.array)
    frame.setflags(write=1)
    frame_expanded = np.expand_dims(frame, axis=0)
    (boxes, scores, classes, num) = sess.run([detection_boxes, detection_scores, detection_classes, num_detections],feed_dict={image_tensor: frame_expanded})
    vis_util.visualize_boxes_and_labels_on_image_array(frame, np.squeeze(boxes), np.squeeze(classes).astype(np.int32), np.squeeze(scores), category_index, use_normalized_coordinates=True, line_thickness=8, min_score_thresh=0.40)
    cv2.putText(frame,"ROOM: {0}".format(ROOM_ID),(30,50),font,1,(255,255,0),2,cv2.LINE_AA)
    countAndSend(np.squeeze(scores), np.squeeze(classes).astype(np.int32), frame)
    cv2.imshow('People detector', frame)
    if cv2.waitKey(1) == ord('q'):
        break
    rawCapture.truncate(0)

camera.close()
cv2.destroyAllWindows()





