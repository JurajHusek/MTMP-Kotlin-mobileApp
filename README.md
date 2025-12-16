# MTMP-Kotlin-mobileApp

**Android Projectile Motion Application (Client–Server)**  
Kotlin-based Android mobile application for simulating and visualizing projectile motion, developed as part of the **I-MTMP course** at **FEI STU**.

---

## Project Overview

This repository contains an **Android mobile client application** for the Projectile Motion assignment from the *I-MTMP (Mobile Technologies and Telematics)* course.

The application allows users to:

- Enter **initial velocity** and **launch angle**
- Send input parameters to a server
- Receive computed trajectory data
- Visualize the results through:
  - **Numerical output** (time, x-position, y-position)
  - **Animation** of the projectile motion
  - **Graph of vertical position y as a function of time y(t)**

The application is implemented in **Kotlin** using **Android Studio**.

---

## Features

- User input for simulation parameters
- Client–server communication
- Numerical trajectory output
- Real-time projectile animation
- Graph visualization of y(t)
- Simple and intuitive user interface

---

## Repository Structure
``` 
├── app/ # Android application module
├── Server/ # Server-side implementation FastAPI Python (trajectory computation)
├── .idea/ # IDE configuration
├── build.gradle.kts # Gradle project configuration
├── settings.gradle.kts
├── Screen_recording_*.webm # Application demonstration
├── mtmp_xhusek_formular.pdf # Assignment specification
└── ...
``` 

---

## Technologies Used

- **Kotlin** – Android application development
- **Android Studio** – Development environment
- **Gradle (Kotlin DSL)** – Build system
- Android SDK UI and networking components
- Python FastAPI for serverside computation

---

## Author

**Bc. Juraj Hušek**  
Faculty of Electrical Engineering and Informatics  
Slovak University of Technology in Bratislava (FEI STU)  
Course: **I-MTMP – Multimedia and Telematics**


