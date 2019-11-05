import RPi.GPIO as GPIO

import time



GPIO.setmode(GPIO.BCM)



sensor = 17

#led = 27
servo = 25



#GPIO.setmode(GPIO.BOARD)
GPIO.setup(sensor, GPIO.IN, pull_up_down = GPIO.PUD_UP)
GPIO.setup(servo,GPIO.OUT)
#GPIO.setup(led, GPIO.OUT)


p=GPIO.PWM(servo,50)# 50hz frequency



p.start(5)# starting duty cycle ( it set the servo to 0 degree )


#Function executed on signal detection

def active(null):
        print("signal receive")
        #GPIO.output(led,GPIO.HIGH)


        p.ChangeDutyCycle(9)
        time.sleep(5)
        p.ChangeDutyCycle(5)

#On detecting signal (falling edge), active function will be activated.

GPIO.add_event_detect(sensor, GPIO.FALLING, callback=active, bouncetime=100)



# main program loop

try:

        while True:
                #GPIO.output(led,GPIO.LOW)
                time.sleep(1)



# Scavenging work after the end of the program

except KeyboardInterrupt:

        GPIO.cleanup()
