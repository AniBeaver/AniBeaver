<img alt="Profile Banner" src="https://github.com/user-attachments/assets/b15885b6-671c-4011-aec5-c2b37f4d4762" width="100%" />
&nbsp;

AniBeaver is a simple and straight-forward Anime Tracker for Windows, macOS and Linux. Built with Kotlin and Compose Multiplatform (Work in progress).
We strive to build an application which has good user experience, modern and clean UI and allows syncing your data with AniList.

If you want to help build the project (implementation, design, or just giving feedback/ideas) or want to chat with us, feel free to join our [Discord server](https://discord.gg/fknGNDGKJB). Keep in mind that we are currently in the process of building out an MVP, so we might not accept any external code contributions for the time being.

## 🚩 Roadmap

You can view our detailed roadmap and feature progress on our [GitHub Project Board](https://github.com/AniBeaver/AniBeaver/projects).

**Current highlights:**
- [x] Base app setup (Compose Multiplatform)
- [x] Create general navigation (different pages)
- [ ] Add Anime entries (title, genre, year, tags, ...) — entry creation popup modal
- [ ] Sync Anime entries with AniList
- [ ] Autocomplete Anime entries with data fetched from AniList API
- [ ] Remove Anime entries
- [ ] Edit Anime entries


## 🏗 Architecture Overview

AniBeaver is a multiplatform application built primarily in Kotlin using [JetBrains Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/). Here’s a high-level overview of the architecture:

- **UI Layer:** 
  - Built with Compose Multiplatform, providing native-like UI for Windows, macOS, and Linux.
  - Navigation is handled using Jetpack Navigation Compose, with major screens for Home, Anime, Manga, Settings, and more.
  - Theming supports both light and dark modes with custom Material 3 color schemes.

- **Data & Logic:**
  - Core logic is implemented in Kotlin common modules, ensuring code sharing across platforms.
  - Uses `DataWrapper` to manage app-wide dependencies and state.
  - Handles user data and session management in a platform-agnostic way.

- **Networking:**
  - Uses Ktor as the HTTP client for API requests.
  - Serialization is handled via kotlinx.serialization.
  - Integrates with the AniList GraphQL API for syncing and fetching anime data.
  - API requests and responses are managed by the `ApiHandler` class, which wraps authentication and GraphQL operations.

- **Project Structure:**
  - `composeApp/` contains the shared UI and business logic.
  - `src/commonMain/` holds platform-agnostic code.
  - `src/desktopMain/` includes desktop-specific entry points and integrations.

- **Build System:**
  - Managed with Gradle Kotlin DSL.
  - Supports native distribution formats (DMG for macOS, MSI for Windows, DEB for Linux).

## 🛠 Technologies Used

- Kotlin (Multiplatform)
- JetBrains Compose Multiplatform
- Ktor (HTTP client)
- kotlinx.serialization (JSON serialization)
- AniList API (GraphQL)
- Material 3 design system

## 🤝 Contributing

We welcome contributions of all kinds! To get started, check out our [GitHub Project Board](https://github.com/AniBeaver/AniBeaver/projects) for ongoing tasks, or join our [Discord server](https://discord.gg/fknGNDGKJB) for discussions.

---
Licensed under the GPLv3 license.  
Copyright (c) 2025 KaiFireborn, darius-it, MatejMinar-jpg
