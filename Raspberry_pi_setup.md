Setting up raspberry:
    + update system and install libraries 
        - sudo apt-get upgrade
        - sudo apt-get install libusb-dev libdbus-1-dev libglib2.0-dev libudev-dev libical-dev libreadline-dev
    + install bluez 
        - sudo wget http://www.kernel.org/pub/linux/bluetooth/bluez-5.49.tar.xz
        - sudo unxz bluez-5.49.tar.xz 
        - sudo tar xvf bluez-5.49.tar
        - sudo ./configure --disable-systemd
        - sudo make
        - sudo install
    + setup ble
        - hciconfig --> one device which should be on "up running"
        - if not than
            * sudo hciconfig hci0 up
            * sudo hciconfig hci0 leadv 3
            * sudo hciconfig hci0 noscanc
            * hciconfig --> shoud be running now

            note: "sudo hciconfig hci0 leadv 3" and "sudo hciconfig hci0 noscanc" switch it to non-connectable mode. This is important otherwise it will not broadcast

Advertise with the PiBeacon:
    + sudo hcitool -i hci0 cmd 0x08 0x0008 1E 02 01 1A 1A FF 4C 00 02 15 | E2 0A 39 F4 73 F5 4B C4 A1 2F 17 D1 AD 07 A9 61 | 00 C8 00 3C C8 00
    + UUID Generated with www.uuidgenerator.net --> 34beba5d-1098-481c-8ce6-f934f9ca8c7a
    + sudo hcitool -i hci0 cmd 0x08 0x0008 1E 02 01 1A 1A FF 4C 00 02 15 34 BE BA 5D 10 98 48 1C 8C E6 F9 34 F9 CA 8C 7A 00 C8 00 3C C8 00

Script to start beacon on start up of raspberry:
    + The content of the shell script

    #!/bin/sh
    set -x

    echo 'make sure that BL is up'
    sudo hciconfig hci0 up

    echo 'put device in non-connectable mode'
    sudo hciconfig hci0 leadv 3
    hciconfig hci0 noscanc

    echo 'start broadcasting with UUID 34beba5d-1098-481c-8ce6-f934f9ca8c7a'
    sudo hcitool -i hci0 cmd 0x08 0x0008 1E 02 01 1A 1A FF 4C 00 02 15 34 BE BA 5D 10 98 48 1C 8C E6 F9 34 F9 CA 8C 7A 00 C8 00 3C C8 00

    + Add shell script to /etc/rc.local
      Just add it like you start a normal script. In our case ./home/pi/startiBeacon.sh

http://www.bluez.org/download/

https://www.makeuseof.com/tag/build-diy-ibeacon-raspberry-pi/m
