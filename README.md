
# 🌾 Grama-Vaxi (ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ)

Grama-Vaxi is a smart Android application developed to improve livestock healthcare management and emergency veterinary support in rural communities. The app digitally connects farmers and veterinarians through a real-time platform that helps manage animal health records, vaccination schedules, and emergency medical assistance.

In many rural areas, farmers face difficulties in maintaining livestock health records, remembering vaccination dates, and getting quick veterinary help during emergencies. Grama-Vaxi solves these problems by providing an easy-to-use mobile solution with support for both English and Kannada languages.

---

# 📌 Project Overview

The application mainly supports three types of users:

## 🚜 Farmers
- Create digital profiles for livestock
- Upload animal photos
- Track vaccination schedules
- Receive reminder notifications
- Report animal health emergencies

## 🩺 Veterinarians
- Monitor emergency reports from farmers
- Provide first-aid advice in real time
- Track pending and resolved cases

## 👨‍💼 Admins
- Manage users
- Organize vaccination drives
- Broadcast community health camp updates

---

# ✨ Key Features

## 🐄 Animal Ledger
Farmers can store livestock details including:
- Animal type
- Breed
- Age
- Vaccination history
- Photos

## 💉 Vaccine Calendar
- Automatically schedules vaccinations
- Sends reminder notifications
- Helps prevent missed vaccinations

## 🚨 Emergency Reporting
Farmers can instantly report:
- Animal symptoms
- Health emergencies
- Medical concerns

Veterinarians receive these reports immediately and respond with treatment suggestions or first-aid instructions.

## 🌐 Multilingual Support
The app supports:
- English
- Kannada (ಕನ್ನಡ)

## ☁️ Cloud Synchronization
- Real-time data syncing using Firebase
- Secure cloud backup
- Access across multiple devices

---

# 🛠 Technology Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Android Development |
| Jetpack Compose | UI Development |
| Material 3 | Modern UI Design |
| MVVM Architecture | Clean Project Structure |
| Firebase Authentication | Secure Login |
| Firebase Firestore | Real-time Database |
| Room Database | Offline Storage |
| WorkManager | Notifications & Background Tasks |
| Coil | Image Loading |

---

# 🧱 Architecture

The project follows the MVVM (Model-View-ViewModel) architecture.

```text
UI Layer (Jetpack Compose)
        ↓
ViewModel Layer
        ↓
Repository Layer
        ↓
Firebase + Room Database
```

---

# 🚀 Working Process

## 1️⃣ User Registration
Users register and select their role:
- Farmer
- Veterinarian

## 2️⃣ Livestock Management
Farmers add livestock details and vaccination records.

## 3️⃣ Vaccination Tracking
The app automatically schedules and reminds users about upcoming vaccinations.

## 4️⃣ Emergency Reporting
Farmers report animal health issues through the app.

## 5️⃣ Veterinary Assistance
Veterinarians view reports instantly and provide emergency guidance.

## 6️⃣ Resolution
Farmers follow the instructions and mark the case as resolved.

---

# 📱 Main Screens

- Splash Screen
- Login & Registration
- Farmer Dashboard
- Veterinarian Dashboard
- Animal Ledger
- Vaccine Calendar
- Emergency Reporting
- Community Health Camps
- Settings & Language Selection

---

# 🔒 Authentication

Grama-Vaxi uses Firebase Authentication for:
- Secure login
- Role-based access
- User session management

---

# 📂 Project Structure

```text
com.example.grama_vaxi/
│
├── data/                             # Data Layer
│   ├── Animal.kt                     # Room Entity for livestock
│   ├── Models.kt                     # Firestore Data Models (DiseaseReport, Camp)
│   ├── AnimalDao.kt                  # Room Database Access Object
│   ├── AnimalRepository.kt           # Repository for local & remote data abstraction
│   └── GramaVaxiDatabase.kt          # Main Room Database configuration
│
├── ui/                               # Presentation Layer
│   ├── screens/                      # Jetpack Compose Screens
│   │   ├── FarmerScreen.kt           # Dashboard for livestock management
│   │   ├── VetDashboardScreen.kt     # Dashboard for veterinarians
│   │   ├── ReportDiseaseScreen.kt    # Emergency symptom reporting
│   │   ├── AnimalDetailsScreen.kt    # Detailed livestock health profile
│   │   ├── RegisterAnimalScreen.kt   # Form to add new livestock
│   │   ├── LoginScreen.kt            # Multi-role authentication
│   │   ├── ProfileScreen.kt          # User settings & app preferences
│   │   ├── AnimalViewModel.kt        # Logic for animal & report management
│   │   └── LanguageViewModel.kt      # Localization logic (EN/KN)
│   │
│   ├── theme/                        # Material 3 Theming & Colors
│   └── Navigation.kt                 # Compose Navigation Graph & Routes
│
├── worker/                           # Background Processing
│   └── VaccineReminderWorker.kt      # Scheduled notifications for vaccinations
│
├── MainActivity.kt                   # Root activity & Navigation entry point
│
└── GramaVaxiApplication.kt           # Application class & Repository initialization
```

## 🧱 Architecture Overview

The project follows the **MVVM (Model-View-ViewModel)** architecture pattern for better scalability, maintainability, and clean separation of concerns.

### 📦 Data Layer
Handles:
- Local database operations using Room
- Cloud synchronization with Firebase Firestore
- Repository abstraction for clean data access

### 🎨 UI Layer
Built completely using:
- Jetpack Compose
- Material 3 Design

Includes:
- Farmer dashboards
- Veterinarian dashboards
- Emergency reporting screens
- Authentication and profile management

### ⚙️ Worker Layer
Uses WorkManager to:
- Schedule vaccination reminders
- Run background notification tasks efficiently

### 🚀 Core Components
- `MainActivity.kt` → Entry point of the application
- `GramaVaxiApplication.kt` → Initializes repositories and application-wide dependencies
  
# ⚙️ Installation

## Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- Firebase Project Setup

## Clone Repository

```bash
git clone https://github.com/your-username/Grama-Vaxi.git
```

## Firebase Setup
1. Create a Firebase Project
2. Enable:
   - Firebase Authentication
   - Cloud Firestore
3. Download `google-services.json`
4. Place it inside:

```text
app/google-services.json
```

## Run the App

```bash
Shift + F10
```

or click **Run ▶ App** in Android Studio.

---

# 🌟 Future Enhancements

- AI-based disease prediction
- Voice support in Kannada
- GPS-based nearby veterinarian detection
- Telemedicine support
- Offline emergency reporting
- Livestock health analytics dashboard

---

# 🎯 Target Users

- Farmers
- Veterinarians
- NGOs
- Government Livestock Departments
- Animal Health Workers

---

# 🤝 Contribution

Contributions are welcome.

## Steps
1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to your branch
5. Open a Pull Request

---

# 📜 License

This project is licensed under the MIT License.

---

# 👨‍💻 Developed By

**Venu S**

Android Developer | Kotlin Enthusiast | AI & Rural Tech Innovator

---

# ❤️ Vision

> “Empowering rural livestock healthcare through accessible technology and real-time veterinary support.”
````
