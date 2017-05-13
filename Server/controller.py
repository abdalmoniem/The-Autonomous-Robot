import math

def heading_controller(my_heading, heading_to, testing = False):
	Kp = 60 #or 30
	rad = math.pi / 180
	
	theta_error = math.atan2(math.sin((heading_to - my_heading) * rad), math.cos((heading_to - my_heading) * rad))
	
	rotation = Kp*theta_error
	if not testing:
		velocity = math.cos(theta_error) * 100
	else:
		velocity = 0
	motor0_speed = rotation + velocity
	motor1_speed = rotation - velocity
    
	return motor0_speed, motor1_speed
