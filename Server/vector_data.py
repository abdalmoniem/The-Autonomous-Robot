##convert lat and mag to a vector
##function operates in decimals degree
##heading is a clock wise angle from north
##choose forward "F" or backward "B" or ignore "I"


import math

def geo_vector(lat1, long1, lat2, long2, typeof):
    if typeof == "I":
        return -1, -1

    my_location = (lat1, long1)
    target = (lat2, long2)
    radius = 6371000

    lat1 = math.radians(my_location[0])
    lat2 = math.radians(target[0])

    diffLat = lat2 - lat1
    diffLong = math.radians(target[1] - my_location[1])

    a = math.pow(math.sin(diffLat / 2), 2) + math.cos(lat1) * math.cos(lat2) * math.pow(math.sin(diffLong / 2), 2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    distance = radius * c

    x = math.sin(diffLong) * math.cos(lat2)
    y = math.cos(lat1) * math.sin(lat2) - (math.sin(lat1) * math.cos(lat2) * math.cos(diffLong))
    initial_bearing = math.atan2(x, y)
    initial_bearing = math.degrees(initial_bearing)
    compass_bearing = (initial_bearing + 360) % 360

    if typeof=="B":
        compass_bearing = compass_bearing + 180
        if compass_bearing >= 360:
            compass_bearing = compass_bearing - 360
    
    return distance, compass_bearing
