<img alt="Profile Banner" src="https://github.com/user-attachments/assets/62da9a63-7197-44cb-9f36-371509dcbe3c" width="100%" />
&nbsp;


AniBeaver is a simple and straight-forward Anime Tracker for Windows, macOS and Linux. Built with Kotlin and Compose Multiplatform (Work in progress).
We strive to build an application which has good user experience, modern and clean UI and allows syncing your data with AniList.

If you want to help build the project (implementation, design, or just giving feedback/ideas) or want to chat with us, feel free to join our [Discord server](https://discord.gg/fknGNDGKJB). Keep in mind that we are currently in the process of building out an MVP, so we might not accept any external code contributions for the time being.

## Quick Overview

With AniBeaver, you can keep track of anime you watched and manga you've read by adding them as Entries. Along with your own rating and custom information like name, year released, studio/author etc, you can filter your entries by custom tags, set your own cover art, etc, track your episode/chapter progress, etc.
By default, Entries are grouped by your current status like "Watching", "Planning" or "Dropped".

Everything from the animation studios to the cover and banner artworks can be automatically ineferred and from the AniList database via a smart Reference feature. Optionally, an entry can have references to multiple seasons of the same series.

With Quick Add, all you have to do is type in an anime name, and a Reference (along with all inferrable information) will be autocompleted. All that's left is to leave your rating, progress/status and Confirm!

AniBeaver is offline-first, which means it is used locally on your computer and never records or uploads your data - only thing it does with the internet is pull data on anime entries for the autofill from AniList.

This is a Beta version. There are plans for more functionality (sync with AniList, better navigation, UI rework, etc) and more compatability (Android, iOS beside Windows, Linux and MacOS support). Please Export from Settings to back-up your entry collection! Just in case. 

Any and all concerns/suggestions with functionality/user experience/potential bugs are welcome in our Discord.


## Roadmap

You can view our detailed roadmap and feature progress on our [GitHub Project Board](https://github.com/AniBeaver/AniBeaver/projects).

**Current action items:**
- [x] Base app setup (Compose Multiplatform)
- [x] Create general navigation (different pages)
- [x] Add Anime entries (title, genre, year, tags, ...) — entry creation popup modal
- [ ] Autocomplete Anime entries with data fetched from AniList API
- [x] Remove Anime entries
- [x] Edit Anime entries

## Tech Stack

- Kotlin (Multiplatform)
- JetBrains Compose Multiplatform
- Ktor (HTTP client)
- kotlinx.serialization (JSON serialization)
- AniList API (GraphQL)
- Material 3 design system

## Contributing

We welcome contributions of all kinds! To get started, check out our [GitHub Project Board](https://github.com/AniBeaver/AniBeaver/projects) for ongoing tasks, or join our [Discord server](https://discord.gg/fknGNDGKJB) for discussions.

---
Licensed under the GPLv3 license.  
Copyright (c) 2025 KaiFireborn, darius-it, MatejMinar-jpg
