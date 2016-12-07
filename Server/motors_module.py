#!/usr/bin/env python
import serial
import time

port = serial.Serial("/dev/ttyUSB0", baudrate=9600, timeout=3.0)
port.write(bytearray([0xAA]))

def control_motor(motor_num , speed):
	#check the motor number within the motors or not
	if not motor_num in (0,1):
		print "invalid motor number , put 0 or 1"
		return
	
	###################################################
	
	#make sure speed within the range -100 to 100
	if speed < -100:
		speed = -100
	elif speed > 100:
		speed = 100

	###################################################
	
	real_speed = int(1.27*abs(speed))	#change the speed from % to real value

	###################################################

	#move the motor with the speed and the direction given
	if speed>0:	#move forward
		if motor_num == 0:
			port.write(bytearray([0x88,real_speed]))
		elif motor_num == 1:
			port.write(bytearray([0x8C,real_speed]))
	
	elif speed<0:    #move reverse
                if motor_num == 0:
                        port.write(bytearray([0x8A,real_speed]))
                elif motor_num == 1:
                        port.write(bytearray([0x8E,real_speed]))

	else:     	 #brake		
                if motor_num == 0:
                        port.write(bytearray([0x86,0]))
                elif motor_num == 1:
                        port.write(bytearray([0x87,0]))
