# Currency Exchange App

A simple currency exchange application built with modern Android development tools and practices.

## Features

- Display current exchange rates for selected currencies.
- Convert between different currencies.
- Search functionality for finding specific currencies.
- Offline support: Cached exchange rates are used when offline or to limit API calls.

## Tech Stack

- **Kotlin**: The primary language for development.
- **Jetpack Compose**: For building the UI.
- **Dagger Hilt**: For dependency injection.
- **Retrofit**: For network requests.
- **Coroutines**: For asynchronous programming.
- **Navigation Compose**: For navigation.

## API

This app uses the [currencylayer.com](https://currencylayer.com/documentation) API for fetching currency data. Note that the free plan of currencylayer.com allows for up to 100 API calls per month. Make sure to monitor your API usage to avoid hitting this limit.

## Getting Started

### Prerequisites

- Android Studio Jellyfish or later.
- A free API key from [currencylayer.com](https://currencylayer.com/documentation).

### Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/phyoemaungmaunghtun/Wave_Currency.git
    cd Wave_Currency
    ```

2. Open the project in Android Studio.

3. Add your currencylayer.com API key to the `gradle.properties` file:
    ```
    API_KEY=your_api_key_here
    ```

### Running the App

1. Build and run the app on an emulator or a physical device using Android Studio.

## Usage

- **Selecting a Currency**: Click on the currency dropdown to select a currency.
- **Entering an Amount**: Enter the amount you want to convert.
- **Viewing Exchange Rates**: The app will display the converted amount for various currencies.
- **Searching for a Currency**: Use the search bar to find specific currencies.

## Limitations

- The app is currently limited to 100 API calls per month due to the free tier of the currencylayer.com API.
- Exchange rates are cached and refreshed no more frequently than every 30 minutes to limit bandwidth usage.

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern.

### Key Components

- **Hilt**: For Dependency Injection.
- **Compose**: For UI components.
- **Retrofit**: For network operations.
- **Coroutines**: For managing background tasks.
- **Navigation Compose**: For handling navigation between screens.

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes. Make sure to follow the existing coding style and include appropriate tests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- Thanks to [currencylayer.com](https://currencylayer.com) for providing the currency data API.

