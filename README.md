# Location Reminder

A Todo list app with location reminders that remind the user to do something when he reaches a specific location. The app will require the user to create an account and login to set and access reminders.

## Getting Started

1. Clone the project to your local machine.
2. Open the project using Android Studio.


### Installation

Step by step explanation of how to get a dev environment running.

1. To enable Firebase Authentication:
        a. Go to the authentication tab at the Firebase console and enable Email/Password and Google Sign-in methods.
        b. download `google-services.json` and add it to the app.
2. To enable Google Maps:
    a. Go to APIs & Services at the Google console.
    b. Select your project and go to APIs & Credentials.
    c. Create a new api key and restrict it for android apps.
    d. Add your package name and SHA-1 signing-certificate fingerprint.
    c. Enable Maps SDK for Android from API restrictions and Save.
    d. Copy the api key to the `google_maps_api.xml`
3. Run the app on your mobile phone or emulator with Google Play Services in it.


## Built With

* [Android Studio](https://developer.android.com/studio) - Default IDE used to build android apps
* [Kotlin](https://kotlinlang.org/) - Default language used to build this project
* [Navigation Component](https://developer.android.com/guide/navigation/navigation-getting-started) - Android Jetpack's Navigation component, used for Fragment-based navigation 
* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - a collection of libraries that help design robust, testable, and maintainable apps: Room (a SQLite object mapping library), LiveData (builds data objects that notify views when the underlying database changes), ViewModel (stores UI-related data that isn't destroyed on app rotations)

## App Screenshots

<img src="https://user-images.githubusercontent.com/33599053/106484219-b09bff80-64af-11eb-9a63-989dd619b14b.png" width=30% height=30%> 
<img src="https://user-images.githubusercontent.com/33599053/106484229-b2fe5980-64af-11eb-9df4-bfd6d588f4df.png" width=30% height=30%> 
<img src="https://user-images.githubusercontent.com/33599053/106484239-b560b380-64af-11eb-9bc7-185a5675d69e.png" width=30% height=30%> 
<img src="https://user-images.githubusercontent.com/33599053/106484244-b691e080-64af-11eb-8516-bd81da692526.png" width=30% height=30%> 
<img src="https://user-images.githubusercontent.com/33599053/106484251-b72a7700-64af-11eb-9d07-91959df86dcd.png" width=30% height=30%> 
