# Base Project

This project showcases efficient network callbacks, media selection, runtime permission handling,
and network requests using Kotlin Flow and coroutines, alongside RX Java. It is fully compatible
with Android 14, ensuring the latest features and security updates are utilized.

## Project Setup

### 1. Prerequisites

- **Android Studio**: Make sure you have the latest version of Android Studio installed.
- **API Key**: You need to create an `apikeys.properties` file in the root of the project with your
  API key.

### 2. Getting an API Key

1. Visit [DummyAPI.io](https://dummyapi.io/) to get your app ID.
2. Create an account or log in if you already have one.
3. Create a new application and note down the application ID provided.

### 3. Creating `apikeys.properties`

Create a file named `apikeys.properties` in the root of your project and add the following line,
replacing `your_app_id` with the application ID you obtained:

```
APP_ID = your_app_id
```

## Features

### 1. Native Splash Screen API

- Implemented using the latest Native Splash Screen API.
-

Dependency: [androidx.core:core-splashscreen:1.0.1](https://mvnrepository.com/artifact/androidx.core/core-splashscreen)

### 2. Network Callbacks

- Handled using Kotlin Flow and coroutines.
- Ensures efficient and reactive network state management.

### 3. Media Picker

- Includes code for picking media (images/videos) from the gallery.
- Provides a user-friendly interface for media selection.

### 4. Runtime Permissions

- Implements runtime permission handling.
- Ensures a smooth user experience by requesting necessary permissions at runtime.

### 5. Network Requests

- Network requests can be made using either Coroutines or RX Java.
- Provides flexibility to choose between the two based on your preference.
- Both implementations are provided in the codebase.

### 6. Gradle Version Catalogs

- This project utilizes the latest Gradle version catalogs.
- Version catalogs offer a convenient way to manage dependencies and their versions.
- This approach enhances maintainability and consistency across the project.

## Compatibility

- All code is compatible with Android 14, ensuring the latest features and security updates are
  utilized.

## Getting Started

### Prerequisites

- Android Studio Jellyfish or higher
- Kotlin 1.9.23

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/smanoj7032/Base.git

### Screenshots

Here are the main screens of the application:

<div style="display: flex; justify-content: space-between;">
  <img src="https://github.com/user-attachments/assets/c45d29f8-73aa-4d44-a3ed-479046175603" alt="Splash Screen" width="250"/>
  <img src="https://github.com/user-attachments/assets/79f56ef2-9b0e-4163-b645-cc65c0d144f3" alt="Login Screen" width="250"/>
  <img src="https://github.com/user-attachments/assets/47bc4c4b-93c1-4c51-9f00-a69ba931c521" alt="Home Screen" width="250"/>
</div>

### Note

This project represents my learning and hard work in Android development. I welcome any feedback and
appreciate your understanding for any mistakes I may have made.
A special thanks to the official Android team for their amazing resources, which have
been crucial to my progress. I also want to express my sincere gratitude to my senior for their
guidance and mentorship.