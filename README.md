# Savit Authenticator open Source

Savit Authenticator is an open source  **Jetpack Compose Only Authenticator App**  deployed using Jenkins CI built to Encourage the use Offline Time / Counter Based One Time Passwords. 

# Run Project (Without Jenkins Configuration and Firebase Craslytics)

1. Install Android Studio and Open Project.
2. Build and Run
3. OPTIONAL -  Generate QR Codes and Test (Try Out With PyOTP -  https://pyauth.github.io/pyotp/) 

# Run Project (With Jenkins Configuration,Firebase Craslytics, Test lab)
 1. Link Prject to Firebase and Configure Firebase Crashlytics
 2. Install Gcloud on the Jenkins Server (Server on Which jenkins is Installed)
 3. Setup Jenkins Project  // link to Setting Up jenkins and Android -  https://medium.com/@jaisonfdo/how-to-setup-ci-cd-for-android-part-2-d67549c8fba1 
 4. Edit the Jenkins pipeline file (Configure Firebase Testlab for your Project and Run remote tests using gcloud with your service Google Cloud accounts)
 5. Setup WebHooks for Your GitHub Repository to Trigger buidls on Jenkins (In Number 3) - OPTIONAL
 6. Push Code To Your GitHub Repository


# References
HOTP -  https://en.wikipedia.org/wiki/HMAC-based_one-time_password
TOTP - https://en.wikipedia.org/wiki/Time-based_One-Time_Password
HOTP Algorithm - https://datatracker.ietf.org/doc/html/rfc4226
MAC - 

# Screen Shots

![alt text](https://play-lh.googleusercontent.com/R1Gwp6DerJa2UZ9LXJYdmWWsvB0ARActvi6OHLviCAKVQo_KfeMNSBGPocx2YZX86iY=w1366-h569-rw)

# Play Store Link
https://play.google.com/store/apps/details?id=org.savit.savitauthenticator


