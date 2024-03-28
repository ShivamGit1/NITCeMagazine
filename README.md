# NITC MAG-e

NITC MAG-e (NITC e-Magazine) is an Android application designed to facilitate the creation, management, and interaction with articles. It provides various functionalities for users, including searching for articles, reading, liking, commenting, downloading, and submitting articles for review. Articles submitted for review are intended to be posted on the app. This app have various users with specific roles and privileges.

## Types of Users
- **NITC Users:** NIT Calicut Students, faculties, staff including reviewers & editors, logged in the app.
- **Reviewer:** NITC user with reviewer privilege.
- **Editor:** NITC user with reviewer privilege.
- **Guest:** Users who are not from NITC or not logged in to the app.
- **Author:** NITC Users whose article(s) are in the app or submitted to be posted after review.
- **All Users:** Includes NITC users and guests.
- **Admin:** NITC admin, the admin is a user of the app whose credentials have been hardcoded directly in the backend.

## Features
1. **User Authentication (F0):** NITC users are granted enhanced access by signing up or signing in with NITC email ID, ensuring a personalized and secure experience.
2. **Article Search (F1):** All Users can conveniently find articles by entering the desired article title or author name in the search bar.
3. **Read Articles (F2):** All Users can explore and engage with articles seamlessly, fostering a user-friendly reading experience.
4. **Like and Comment on Articles (F3):** NITC Users express appreciation by using the like icon, and they can share thoughts by leaving comments, enhancing user interaction and feedback.
5. **Download Articles (F4):** Articles are made easily accessible by allowing NITC users to download content, enabling offline access and resource sharing.
6. **Author Visibility (F5):** Authors have a comprehensive view of the status of their posts and associated reviews, promoting transparency and accountability.
7. **Reviewer Confirmation (F6):** Reviewers contribute to the quality control process by providing remarks and reviews to proposed articles.
8. **Editor Post or Reject (F7):** Editors play a pivotal role in curating content, having the authority to post or reject articles based on their quality and relevance.
9. **Editor Functionalities (F8):** Editors can efficiently manage articles by performing operations such as posting, editing, or deleting, ensuring content integrity.
10. **Editor Article Analysis (F9):** Editors have a holistic view of all posts and comments, facilitating in-depth analysis and informed decision-making.
11. **Admin Management (F10):** Admin will manage (add/remove) reviewers and manage (add) editors.
12. **Article Categorization (F11):** Articles are organized into categories, streamlining navigation and making it easier for users to discover relevant content.

## Prerequisites
- Android (Minimum SDK 5) device with Android OS installed.
- Internet connectivity.

## Steps to Run the App
1. Install the Android Studio.
2. Run this repository.
3. Create an Android Virtual Device to use the Android Emulator or connect an Android device (mobile).
4. Build and run the app, or download the APK file of the app.
5. Open the downloaded APK file to install the app.

## How to Use
- **Login/Register:** Launch the app on your Android device. If you're a new user, sign up (register) by providing the required details like Full name, profile pic (optional), email (NITC mail credentials), and password. Upon submitting your registration, you will receive an email to verify your email. If you're an existing user, log in (Sign In) using your credentials.
- **Explore Articles:** Once logged in, you can start exploring articles. Use the search bar to find specific articles by entering keywords or titles.
- **Read:** Click on an article to read its content.
- **Like, and Comment:** Express appreciation by using the like icon. Share your thoughts and feedback by leaving comments on articles.
- **Download Articles:** Download articles for offline access by clicking the download icon.
- **Submit Articles for Review:** If you're a NITC student, faculty, reviewer, or  editor, you can submit articles for review. Login to your account and navigate to the submission section or click the "Plus" icon. Fill in the required details and submit your article for review.
- **Review Article(s):** If you're a reviewer, you have the authority to review articles. Login to your account, navigate to the review section by clicking the menu button. You can view the submitted articles and provide ratings and feedback. You can edit articles if required and forward them to the editor by submitting a review. The editor will review the articles and decide whether to post or reject them.
- **Article(s) and its Status:** Authors can find the "my article" section by clicking the menu button to check their article(s) and the status of article(s).
- **Post and Delete Articles:** If you're an editor, you have the authority to approve and post articles. Login to your account, navigate to the editor section by clicking the menu button, and do the needful, post or delete (reject) article(s).
- **Manage Account:** You can manage your account settings, including profile information in the "my profile" section, and change passwords in the "change password" section.
- **Manage Reviewer(s) & Editor:** If you're an admin, you have the authority to manage Reviewer(s) & Editor. Login to your account, navigate to the manage user section by clicking the menu button, and do the required job.
- **Logout:** Once you're done using the app, remember to log out for security purposes.

## Technologies Used
- **Firebase Realtime Database and Firebase Cloud Storage:** Utilized for managing article data and assets, enabling real-time updates and scalable storage solutions.
- **Java:** Used for backend development, providing robust and versatile functionality.
- **XML:** Utilized for frontend design, facilitating the creation of user interfaces for the Android application.
- **Development tool: Android Studio:** The primary integrated development environment (IDE) used for developing the Android application.
- **iText5 PDF library:** Integrated to enable users to conveniently download articles in PDF format for offline access and enhanced accessibility.

## Version Control
- **GitHub:** Utilized for version control, enabling collaborative development and tracking of project changes.

---

### PPT
Images<br>
<img src="PPT_MAGe/mage_01.png" width="410">


**This README file provides a comprehensive overview of the NITC MAG-e Android app, including its features, technology used, prerequisites, steps to run the app, usage instructions, and version control.**
