# 🎙️ Sarthi (सारथी)
### *Empowering Bharat: The Offline-First Bilingual Voice Bridge*

[![Hackathon Project](https://img.shields.io/badge/Hackathon-Project-orange?style=for-the-badge)](https://github.com/Yuvraj1954/Sarthi)
[![Platform](https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](https://opensource.org/licenses/MIT)

---

## 📺 Project Showcase
**Sarthi** is a native Android assistant built to solve the "Connectivity Gap" in rural India. It provides life-critical information via voice, even when the internet is completely unavailable.

### [🎥 Watch the Demo Video](https://www.youtube.com/shorts/b9R8nyTjsQw)

---

## 📱 App Walkthrough
| Interface | Healthcare Info | Govt Schemes | Settings |
| :---: | :---: | :---: | :---: |
| <img src="screenshots/screen1.png" width="200" /> | <img src="screenshots/screen2.png" width="200" /> | <img src="screenshots/screen3.png" width="200" /> | <img src="screenshots/screen4.png" width="200" /> |
| *Bilingual Entry* | *Offline Recognition* | *Instant Health Tips* | *Scheme Details* |

> **Note to Judges:** To view these images, create a folder named `screenshots` in your repo and name your images `screen1.png`, `screen2.png`, etc.

---

## 💡 The Problem: The "Triple Barrier"
In rural India, 300M+ users face:
1. **The Literacy Barrier:** Text-heavy apps are unusable for many.
2. **The Language Barrier:** Information is rarely available in local dialects.
3. **The Connectivity Barrier:** 4G/5G is unreliable; apps fail in "Data-Dark" zones.

**Sarthi is the solution.** It turns a smartphone into a talking companion that doesn't need a stable signal to save a life.

---

## 🚀 Key Technical Features

### 📡 1. Resilient Offline Architecture
Unlike standard AI assistants that require an API call for every word, Sarthi uses:
* **On-Device STT:** Local Speech-to-Text processing.
* **Room Database:** A pre-loaded local "Knowledge Bank" of essential services.
* **Zero-Latency Response:** Instant feedback for medical emergencies and scheme queries.

### 🔄 2. Intelligent Cloud Synchronization
Built using the **Android WorkManager API**, Sarthi intelligently queues user interactions. The moment a 2G/3G connection is detected, it background-syncs data without interrupting the user experience.

### 🗣️ 3. Bilingual NLP Logic
Engineered to understand **Hinglish** (Hindi + English mixed), ensuring the UI feels natural to users who don't speak formal "pure" languages.

---

## 🛠️ Built With
* **Language:** Kotlin / Java
* **Database:** SQLite with Room Persistence Library
* **Background Processing:** WorkManager API
* **Voice Engine:** Android SpeechRecognizer & Text-To-Speech (TTS)
* **Architecture:** MVVM (Model-View-ViewModel) for scalability

---

## 🔧 Installation & Setup
1. Clone the repository:
   ```bash
   git clone [https://github.com/Yuvraj1954/Sarthi.git](https://github.com/Yuvraj1954/Sarthi.git)
