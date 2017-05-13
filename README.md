# The Autonomous Robot

![The Autonomous Robot Screenshot](https://raw.githubusercontent.com/abdalmoniem/The-Autonomous-Robot/master/assets/robot.jpg)

A simple autonomous robot that plays capture the flag.

## Platform:
The robot's brains is the `Raspberry Pi 3` but it can run on bretty much any linux platform given that it supports the attached hardware on the robot.

## Hardware:
The robot's hardware consists of:

1. 4x 6v 133 RPM DC Motors.
2. 1x Adafruit GPS breakout board.
3. 1x Adafruit BNO555 IMU board.
4. 1x Pololu Qik2s12v10 dual motor controller board.
5. 1x Step down DC-DC converter.
6. 1x Raspberry Pi 3 board.
7. 1x 7.2v 5200Ah battery pack.

## Software Installation:
1. make sure you you have python 2.7 on your system.
2. install netifaces on your platform

	```shell
	sudo pip install netifaces
	```

3. install pyserial on your platform

	```shell
	sudo apt-get install python-serial
	```

4. install `Robot_App/app/build/outputs/apk/app-debug.apk` on your android device for manual and semi-manual control. [optional]

## How does it work:
The map is given to the robot as gps coordinates and boundaries and it uses `Vector Fields Histogram` to make sure it is within these boundaries and away from the obstaciles.

The robot utilizes the following sensors to stay on track and detect goals and other players it wants to avoid.

1. GPS coordinates
2. Linear acceleration
3. Speed

## Vector Fields Histogram wiki:
In robotics, Vector Field Histogram (VFH) is a real time motion planning algorithm proposed by Johann Borenstein and Yoram Koren in 1991. The VFH utilizes a statistical representation of the robot's environment through the so-called histogram grid, and therefore places great emphasis on dealing with uncertainty from sensor and modeling errors. Unlike other obstacle avoidance algorithms, VFH takes into account the dynamics and shape of the robot, and returns steering commands specific to the platform. While considered a local path planner, i.e., not designed for global path optimality, the VFH has been shown to produce near optimal paths. The original VFH algorithm was based on previous work on Virtual Force Field, a local path-planning algorithm. VFH was updated in 1998 by Iwan Ulrich and Johann Borenstein, and renamed VFH+ (unofficially "Enhanced VFH"). The approach was updated again in 2000 by Ulrich and Borenstein, and was renamed VFH*. VFH is currently one of the most popular local planners used in mobile robotics, competing with the later developed dynamic window approach. Many robotic development tools and simulation environments contain built-in support for the VFH, such as in the Player Project.

wiki page: https://en.wikipedia.org/wiki/Vector_Field_Histogram

## How Vector fields work:
The robot calculates, at all times, the distance between itself and the given obstacles and boundaries and produces a vector whoes magnitude and direction determines the motors speed and direction of rotation, it then sums up all the vectors and produces a resultant vector of which it follows as a direction.

## Implementation:
All the implementation was done using `Python2.7`

There are a few files in the project:

- `controller.py`: It is the PID controller of the robot's motors, it is responsible of directing motors rotation and speed.

- `gps_handler.py`: It is responsible of reporting back the robot's `location` so that it can be used for path planning.

- `imu_handler.py`: It is responsible of reporting back the robot's `linear acceleration`, `heading`, `speed` and `time` so that it can be used for path planning.
  on changes.

- `motors_module.py`: It is responsible of communicating with the motor driver so it can output the correct voltage to the motors to make them rotate in the desired direction and speed.

- `vector_data.py`: It is the core implementation of the VFH, it uses data from all other modules to compute the optimal path towards its goal.

- `z_server.py`: It ties everything together, it is responsible of creating and establishing a server that communicates with the other friendlly robot so that they can coordinate their attacks and defenses.

## Manual and Semi-Manual control:
In addition to being fully autonomous, the robot has two more modes of operation:

1. Manual mode: in which the user uses the android app to drive the robot manually [forward, backward, left, right and stop]
2. Semi-Manual mode: in which the user chooses the `maps` option from the android app to give the robot GPS coordinates and the robot goes to it.
