import gps

# Listen on port 2947 (gpsd) of localhost
session = gps.gps("localhost", "2947")
session.stream(gps.WATCH_ENABLE | gps.WATCH_NEWSTYLE)

def get_gps_coordinates(cmd):
    	report = session.next()
        if cmd == "lat":
		     return report.lat
	     elif cmd == "lon":
	 		  return report.lon
