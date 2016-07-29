#!/usr/bin/env python
import sys
import time
import socket
import netifaces as ni
import threading as th

from thread import *
from vector_data import geo_vector
from motors_module import control_motor
from controller import heading_controller
from imu_handler import get_imu_readings
from gps_handler import get_gps_coordinates

speed = 100
target_lat = 0.0
target_lon = 0.0
started = False
autonomus = False
sensors_data = ["null", "null", "null", "null"]

class SigFinish(Exception):
    pass

def get_sensors_data():
	imu_thread = th.Thread(target = get_imu_data)
	imu_thread.daemon = True
	imu_thread.start()

	gps_thread = th.Thread(target = get_gps_data)
	gps_thread.daemon = True
	gps_thread.start()

def get_imu_data():
   while 1:
      try:
         sensors_data[0] = get_imu_readings("lin_acc")
  	      sensors_data[1] = get_imu_readings("heading")
	   except:
		   pass

def get_gps_data():
	while 1:
		try:
			sensors_data[2] = get_gps_coordinates("lat")
			sensors_data[3] = get_gps_coordinates("lon")
		except:
			pass

def move_robot():
	global target_lat
	global target_lon
	try:
		while sensors_data[2] != target_lat and sensors_data[3] != target_lon:
			distance, heading = geo_vector(sensors_data[2], sensors_data[3], target_lat, target_lon, "F")
       			m0, m1 = heading_controller(float(sensors_data[1]), float(heading))

			control_motor(0, m0)
        		control_motor(1, m1)
			time.sleep(0.1)
	except SigFinish:
		print "Stopping motors for new location..."
		control_motor(0, 0)
	       	control_motor(1, 0)

def interrupt_thread(thread):
    for thread_id, frame in sys._current_frames().items():
	if thread_id == thread.ident:  # Note: Python 2.6 onwards
            set_trace_for_frame_and_parents(frame, throw_signal_function)

def throw_signal_function(frame, event, arg):
    raise SigFinish()

def do_nothing_trace_function(frame, event, arg):
    # Note: each function called will actually call this function
    # so, take care, your program will run slower because of that.
    return None

def set_trace_for_frame_and_parents(frame, trace_func):
    # Note: this only really works if there's a tracing function set in this
    # thread (i.e.: sys.settrace or threading.settrace must have set the
    # function before)
    while frame:
        if frame.f_trace is None:
            frame.f_trace = trace_func
        frame = frame.f_back
    del frame

control_thread = th.Thread(target = move_robot)
control_thread.daemon = True

def client_thread(conn, addr):
	global target_lat
	global target_lon
	global started

	#infinite loop so that function do not terminate and thread do not end.
	while True:
		#Receiving from client
		data = conn.recv(1024)
		
		if not not data:
			#check if the user wants to autonomusly move the robot
			if not data.rstrip().find("lat"):
				target_lat = float(data.rstrip().split("lat")[1])
				autonomus = True
			elif not data.rstrip().find("lon"):
            target_lon = float(data.rstrip().split("lon")[1])
				autonomus = True
			else:
				autonomus = False

			#check if the user wants to manually move the robot
			if data.rstrip() == "forward":
				control_motor(0, -speed)
				control_motor(1, speed)
			elif data.rstrip() == "backward":
				control_motor(0, speed)
				control_motor(1, -speed)
			elif data.rstrip() == "right":
				control_motor(0, 0)
				control_motor(1, speed)			
			elif data.rstrip() == "left":
				control_motor(0, -speed)
				control_motor(1, 0)			
			elif data.rstrip() == "stop":
        	   control_motor(0, 0)
            control_motor(1, 0)
		
			if autonomus:
				if sensors_data[1] != "null" and sensors_data[2] != "null" and sensors_data[3] != "null":
					if not started:
						started = True
						control_thread.start()
					else:
						interrupt_thread(control_thread)

				else:
					print "I don't have sufficient data, cant move right now."
		else:
			break

	#came out of loop
	print "Client %s disconnected." %addr[0]
	conn.close()

#############################__Main Thread__#############################
if len(sys.argv) != 3:
	print "Usage: python server.py <interface> <port number>"
	sys.exit(0)

if not sys.argv[2].isdigit() or sys.argv[2] < 0:
	print "port number must be a positive integer."
	sys.exit(0)

try:
	get_sensors_data()

	INTERFACE = sys.argv[1]
	PORT = int(sys.argv[2])
	HOST = ''

	s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
	print "Socket created."
	 
	#Bind socket to local host and port
	s.bind((HOST, PORT))
	print "Socket bind complete."
   
	#Start listening on socket
	s.listen(10)
	ni.ifaddresses(INTERFACE)
	ip = ni.ifaddresses(INTERFACE)[2][0]["addr"]
	print "Server is now up and listening on %s:%d" %(ip, PORT)
except socket.error as msg:
	print "Bind failed."
	print "Error Code: %s"
	print "Message: %s" %(str(msg[0]), msg[1])
	sys.exit()
except ValueError as ve:
	print "Error: %s" %ve
	sys.exit(0)

while 1:
	try:
		#wait to accept a connection - blocking call
		conn, addr = s.accept()
		print "%s is now connected." %addr[0]
	except KeyboardInterrupt:
		print "\nClosing server..."
		print "Stopping motors..."
		s.close()
		control_motor(0, 0)
		control_motor(1, 0)
		print "Server closed."
		print "Motors stopped."
		sys.exit(0)

	start_new_thread(client_thread, (conn, addr))
