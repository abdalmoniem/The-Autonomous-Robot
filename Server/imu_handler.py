import time
import math

from Adafruit_BNO055 import BNO055

bno = BNO055.BNO055(serial_port="/dev/ttyUSB2")

if not bno.begin():
    raise RuntimeError("Failed to initialize BNO055, sensor might not be connected!!")

def get_imu_readings(cmd):
	if cmd == "lin_acc":
		x, y, z = bno.read_linear_acceleration()
		lin_acc = math.sqrt((x**2) + (y**2) + (z**2))
		return str(lin_acc)
	elif cmd == "heading":
		h, r, p = bno.read_euler()
		return str(h)
