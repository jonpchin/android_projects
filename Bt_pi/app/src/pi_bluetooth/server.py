import bluetooth
# Runs on Linux with Python 2.7

uuid="C6A2B7CCB19A42B1A67FA7C28B741B28"

def runServer():
	serverSocket=bluetooth.BluetoothSocket(bluetooth.RFCOMM)
	port=bluetooth.PORT_ANY
	serverSocket.bind(("",port))
	print "Listening for connections on port: ", port   
	serverSocket.listen(1)
	port=serverSocket.getsockname()[1]

	#the missing piece
	bluetooth.advertise_service( serverSocket, "SampleServer",
					   service_id = uuid,
					   service_classes = [ uuid, bluetooth.SERIAL_PORT_CLASS ],
					   profiles = [ bluetooth.SERIAL_PORT_PROFILE ] 
						)

	inputSocket, address=serverSocket.accept()
	print "Got connection with" , address

	while 1:
		data = inputSocket.recv(1024)
		print "Received: %s" % data
		if (data == "0"):    #if '0' is sent from the Android App, turn OFF the LED
			print "0 is pressed"

		if (data == "1"):    #if '1' is sent from the Android App, turn OFF the LED
			print "1 is pressed" 
	 
		if (data == "q"):
			print "Quit"
			break
		
	inputSocket.close()
	serverSocket.close()  

runServer() 