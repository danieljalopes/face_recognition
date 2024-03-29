import numpy as np
import cv2

import calendar
import time

import requests

# multiple cascades: https://github.com/Itseez/opencv/tree/master/data/haarcascades

#https://github.com/Itseez/opencv/blob/master/data/haarcascades/haarcascade_frontalface_default.xml
face_cascade = cv2.CascadeClassifier('.\haarcascade_frontalface_default.xml')
#https://github.com/Itseez/opencv/blob/master/data/haarcascades/haarcascade_eye.xml

url = 'http://localhost:8080/rekognition/face/index'
filename = 'face_capture.png'
cap = cv2.VideoCapture(0)

while True:
		ret, img = cap.read()
		gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
		faces = face_cascade.detectMultiScale(gray, 1.3, 5)
		for(x, y, w, h) in faces:
			cv2.rectangle(img, (x,y), (x+w, y+h), (255,0,0), 2)
			roi_gray = gray[y:y+h, x:x+w]
			roi_color = img[y:y+h, x:x+w]
			

		cv2.imshow('img', img)
		k = cv2.waitKey(30) & 0xff
		if k == ord('q'):
			break
		elif k == ord('c'):
			print("carregou em c")
			#timestamp = calendar.timegm(time.gmtime())
			#cv2.imwrite( str(timestamp) + '.png', roi_color)

			cv2.imwrite( filename, roi_color)
			files = {'foto': open(filename, 'rb')}
			requests.post(url, files=files)

cap.release()
cv2.destroyAllWindows()