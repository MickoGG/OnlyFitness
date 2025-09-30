# OnlyFitness application

### Implementation of an Android application called OnlyFitness.

The application was developed using Kotlin Android Jetpack and Firebase.

## About the application

This is a fitness application where clients can subscribe to trainers in order to receive services such as meal plans, workout plans, and more.

The application uses Firebase Authentication for user authentication and Firebase Firestore Database as the database.

### Key features and functionalities:
- There are two types of users – clients and trainers
- Users can log in/register in the system
- After logging in, the home screen is displayed (the first item in the navigation menu), where users can see a list of all trainers and also use the "Search" option
- Each trainer is clickable, leading to a page with detailed information about that trainer such as biography, achievements, service description (whether they work with both men and women or just one of the two, etc.). There are also buttons to start a chat with that trainer and to subscribe to that trainer
- The navigation menu also includes a profile page with a modern look. On their profile, users can see all clients subscribed to them (if the user is a trainer) and all trainers they are subscribed to. Users can also edit personal information and update their profile picture
- The navigation menu also contains the chat option, which leads to a page where the user can see all their chats and enter any of them to communicate as expected
- The last item in the navigation menu is notifications, where users can see all the notifications they have received

Special attention was also given to the application architecture and to writing clean, maintainable code.
