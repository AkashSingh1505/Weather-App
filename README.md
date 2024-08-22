# Weather App

Welcome to the Weather App! This application provides real-time weather information based on your location. It fetches weather data from an external API and displays it in a user-friendly interface.

## Setup Instructions

To get the Weather App up and running on your local machine, follow these steps:

### 1. Clone the Repository

Clone the repository using Git:

```bash
git clone <repository-URL>
cd <repository-directory>


# Navigate to the root directory of your project
cd <repository-directory>

# Create the gradle.properties file if it does not already exist
touch gradle.properties

# Open the gradle.properties file using a text editor (e.g., nano)
nano gradle.properties

API_KEY="a9d7877ecf28b142eab78b39d9e14c03"

# Sync Gradle (run this command from your project root)
./gradlew build

# Build and run the project (run this command from your project root)
./gradlew installDebug
