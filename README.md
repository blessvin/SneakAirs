# SneakAirs 

This project was completed as the Final Year Capstone Project by:

* Sumod Kulkarni (131090050)
* Pranav Nayak (131090056)
* Prit Gala (131090044)
* Smitesh Modak (131090045)
* Yash Beri (131090047)

## Idea

The idea behind this project was to let users immerse themselves in their surrounding by keeping them from looking into their smartphones. We tackle this problem by introducing a novel way to navigate in a city using improved interactions with a smartphone.


## Workflow

* This project aimed to create a working proof-of-concept of an Android app coupled with a pair of shoes.
* A user using this app first needs to pair the app via bluetooth with the shoes.
* Then the user can select a destination on an implementation of Google Maps.
* The app then fetches directions to the destination from the user's present location using the Google Maps Directions API.
The fetched directions are stored locally on the Android smartphone. 
* Once the user begins navigation, the app tracks the user's location and keeps checking if they have reached an intersection. If so, the app sends a signal via short-range bluetooth connection to the shoes. 
Each shoe has a vibrotactile tickler in it. If the user is to turn left, the left shoe tickles or right shoe if he's to turn right.

* We added an additional functionality of location based reminders.
Users can predeifne a location to be reminded at about something. Say a bus stop.
If the user approaches the location, the app sends a signal and both shoes soft vibrate indicating they have reached the stop and it's time to get down.

NOTE: No computation is done in the shoes and no GPS module is present. The project uses the GPS data available from the user's phone. All of the computational work is done within the app on the phone. The shoes are only programmed to vibrate when a signal is received.

### Prerequisites

What things you need to install the software and how to install them

* Android Studio 2.3 or above
* Java JDK 1.8
* Intel i5 processor or better with 4 GB RAM or above (recommended)


### Hardware parts used for the shoes:

* Arduino Nano microcontroller 
* Bluetooth Module HC-05
* Any standard battery for arduino
* Vibration motors for Arduino


## License

This project has been open-sourced and is licensed under the MIT License - https://opensource.org/licenses/MIT

## Acknowledgments

* This project was inspired from a similar existing project which can be found at: www.dhairyadand.com/works/supershoes

