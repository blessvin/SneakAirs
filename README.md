# SneakAirs 

This is the Final Year Capstone Project submission (May 2017) at Veermata Jijabai Technological Institute by:

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
Users can predefine a location to be reminded at about something. Say a bus stop.
If the user approaches the location, the app sends a signal and both shoes soft vibrate indicating they have reached the stop and it's time to get down.

NOTE: No computation is done in the shoes and no GPS module is present. The project uses the GPS data available from the user's phone. All of the computational work is done within the app on the phone. The shoes are only programmed to vibrate when a signal is received.

### Prerequisites

To download and run the code you need:

* Android Studio 2.3 or above
* Java JDK 1.8
* Intel i5 processor or better with 4 GB RAM or above (recommended)


### Hardware parts used for the shoes:

* [Arduino Nano microcontroller](https://www.amazon.com/gp/aw/d/B0713XK923/ref=mp_s_a_1_3?ie=UTF8&qid=1515420839&sr=8-3&pi=AC_SX236_SY340_QL65&keywords=arduino+nano&dpPl=1&dpID=51d1hIDOGxL&ref=plSrch)
* [Bluetooth Module HC-05](https://www.amazon.com/gp/aw/d/B074GMQ6G3/ref=mp_s_a_1_7?ie=UTF8&qid=1515420732&sr=8-7&pi=AC_SX236_SY340_FMwebp_QL65&keywords=hc05+bluetooth+module&dpPl=1&dpID=5126rOXcenL&ref=plSrch)
* Any standard battery pack for arduino (we used Duracell)
* [Vibration motors for Arduino](https://www.amazon.com/Vibrating-Vibration-Arduino-Projects-9000RPM/dp/B075V4HV31)


## License

This project has been open-sourced and is licensed under the MIT License - https://opensource.org/licenses/MIT

## Acknowledgments

* This project was inspired from a similar existing project which can be found at: www.dhairyadand.com/works/supershoes

