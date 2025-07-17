
# AI Translator App

An Android application that allows users to translate text between 150+ languages using Google ML Kit. Designed with a clean, modern UI (as shown in the Figma design) and built for simplicity, speed, and offline-first support.

Figma Link: https://www.figma.com/design/xZnh1VP9Thv8uOlzTe4k2R/Translator---?node-id=0-1&p=f&t=8xihOKKGm5RRLoYz-0

## Description

This app enables fast and accurate language translation with:
1. Minimal input: 
        Just type or paste the text.
2. Instant output:
         Translated result shown immediately.
3. Clean interface: 
        Simple two-column UI for source and target text.
4. Offline functionality: 
        Translates even without internet after model download.
        
## Technologies Used

1. Kotlin for Core programming language
2. Jetpack ViewModel for State management
3. Google ML Kit for Translation (on-device)
4. Material Design for Clean UI/UX
5. MVVM Architecture for Separation of concerns

## Goal

1. Leverage Google ML Kit's On-Device Translation API.
2. Support 150+ languages.
3. Enable bi-directional translation.
4. Ensure offline usage by downloading models on first-time use.
5. Provide a modern, clean UI based on Figma layout.

## Core Features

1. Translate Text: Translate between source and target languages.
2. Swap Languages: Switch source ↔ target languages with one tap.
3. Offline Models: ML Kit automatically downloads models when needed.
4. ML Kit Integration: Uses Google's on-device translation to 5. ensure privacy.
5. Clear UI: Inspired by the linked Figma layout.

## Working
The app uses Google ML Kit's on-device translation model to provide fast and reliable translations with minimal delay.

Step-by-Step Flow:
1. User Inputs Text
        The user types or pastes text into the source text field.
2. Select Languages
        The user selects:
3. Source language (e.g., English)
4. Target language (e.g., Urdu)
5. Model Download (First Time Only)

        a.  The app checks if the required language model is available on the device:
        b. If not downloaded, it automatically downloads the translation model for offline use.
        c. If already downloaded, it proceeds instantly.
6. Translation Execution
   
        a. The text is passed to the ML Kit's translator:
        b. Translation is performed entirely on-device
        c. No data is sent to servers, ensuring privacy & speed
8. Display Result
        The translated text is displayed immediately in the target text field.

# Screenshots

## Main Page

<p align="center">
<img width="250" height="500" alt="translation_main" src="https://github.com/user-attachments/assets/061027e2-6481-4490-bd74-6d12fd89290c" />
</p>

## Text Transaltion

<p align="center">
<img width="250" height="500" alt="translation_translation" src="https://github.com/user-attachments/assets/555d24a0-7b5b-43ab-a56d-d391c4788fa8" />
</p>

## 1 to 1 Conversation

<p align="center">
<img width="250" height="500" alt="translation_convo" src="https://github.com/user-attachments/assets/22593930-c6be-42ca-a0b7-64db8600c68d" />
</p>

## Live Camera Translation

<p align="center">
<img width="250" height="500" alt="translation_camera" src="https://github.com/user-attachments/assets/05bfe4d6-7be6-48c5-8673-86a8b66767c5" />
</p>
<p align="center">
<img width="250" height="500" alt="translation_translatedImage" src="https://github.com/user-attachments/assets/3cce189a-59ea-4d3b-9810-b29331295367" />
</p>

## Settings

<p align="center">
<img width="250" height="500" alt="translation_settings" src="https://github.com/user-attachments/assets/72bfada8-85ea-419e-a6b4-0c7c4ab115b4" />
</p>

## History

<p align="center">
<img width="250" height="500" alt="translation_history" src="https://github.com/user-attachments/assets/64c285e6-c08d-47de-9d3f-2535327cfff5" />
</p>







