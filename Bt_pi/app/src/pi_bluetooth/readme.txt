From the Raspberry Pi desktop, open a new Terminal window.
Type sudo bluetoothctl then press enter and input the administrator password (the default password is raspberry).
Next, enter agent on and press enter. Then type default-agent and press enter.
Type scan on and press enter one more time. The unique addresses of all the Bluetooth devices around the Raspberry Pi
will appear and look something like an alphanumeric XX:XX:XX:XX:XX:XX. If you make the device you want to pair discoverable
(or put it into pairing mode), the device nickname may appear to the right of the address. If not, you will have to do a
little trial and error or waiting to find the correct device.
To pair the device, type pair [device Bluetooth address]. The command will look something like pair XX:XX:XX:XX:XX:XX.
To connect use connect XX:XX:XX:XX:XX:XX
To remove device use remove XX:XX:XX:XX:XX:XX


[bluetooth]# power on
[bluetooth]# agent on
[bluetooth]# discoverable on
[bluetooth]# pairable on
[bluetooth]# scan on

pair

If you're pairing a keyboard, you will need to enter a six-digit string of numbers. You will see that the device has been paired, but it may not have connected. To connect the device, type connect XX:XX:XX:XX:XX:XX.