# Base Project

This is a base project for Android development using Kotlin. The project is configured to use several plugins and libraries for a smooth development experience. Follow the instructions below to set up the project.

## Project Setup

### 1. Prerequisites

- **Android Studio**: Make sure you have the latest version of Android Studio installed.
- **API Key**: You need to create an `apikeys.properties` file in the root of the project with your API key.

### 2. Getting an API Key

1. Visit [DummyAPI.io](https://dummyapi.io/) to get your app ID.
2. Create an account or log in if you already have one.
3. Create a new application and note down the application ID provided.

### 3. Creating `apikeys.properties`

Create a file named `apikeys.properties` in the root of your project and add the following line, replacing `your_app_id` with the application ID you obtained:

```properties
APP_ID=your_app_id
