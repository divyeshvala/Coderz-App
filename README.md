# Android Internship Task
### Install Instructions
* **Method1** - Clone repository on your PC and launch the project in android studio. Connect your smartphone to PC with USB debugging ON in it. Then run the app from android studio.
* **Method2** - Download apk file named "demux.apk" from root directory of repo. And install it on your device.

### Screenshots
Home Page | Filter Sheet | Search Bar                 
:-------------------------:|:-------------------------:|:-------------------------:
![Home Page](https://github.com/divyeshvala/demux/blob/main/screenshots/home.jpg?raw=true "Home Page")|![Filter Sheet](https://github.com/divyeshvala/demux/blob/main/screenshots/filter.jpg?raw=true "Filter Sheet")|![Search Bar](https://github.com/divyeshvala/demux/blob/main/screenshots/search.jpg?raw=true "Search Bar")
* There is screen recording in screenshots directory. You can download it and watch it.

### Source of the questions data
* Web scrapping (BeautifulSoup) has been used to scrap data from [Leetcode](https://leetcode.com/problemset/all/) and it has been uploaded to my firebase realtime database. Currently there are around 250 questions in firebase database.
* So now this app is loading questions from firebase realtime database.
* Each question contains following data : title, id, company tags, topics, difficulty level, frequency and description(in HTML format).

### Current Features
1. **Display coding questions** - Questions are being displayed from firebase realtime database.
2. **Company & topic tags** - There are company & topic tags on the question which are clickable. On clicking tag, relevant questions will be displayed. Each company has specific colored tag for better user experience.
3. **Filter** - Questions can be filtered based on single or mutliple companies and other general tags like College, Offcampus, Internship, Full-time etc.
4. **Search** - Questions can be searched based on title and tags.

### Technology and frameworks used
1. Java.
2. Android Studio
2. Firebase Realtime Database.
4. Python
5. BeautifulSoup

### Contributors
* [Divyesh Vala](https://github.com/divyeshvala)
