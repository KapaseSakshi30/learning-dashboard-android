# Android Course Learning Application

A robust, scalable Android application built with modern development practices, implementing **Clean Architecture** paired with the **MVVM (Model-View-ViewModel)** design pattern.

---

## 🏗️ 1. Architecture

This project follows Clean Architecture paired with the MVVM (Model-View-ViewModel) pattern to enforce separation of concerns, testability, and maintainability.

```
       +-------------------------------------------------+
       |               Presentation Layer                |
       |      (Fragments, ViewBinding, ViewModels)       |
       +------------------------+------------------------+
                                |
                                v
       +-------------------------------------------------+
       |                  Domain Layer                   |
       |      (Models, Use Cases, Repository Interfaces) |
       +------------------------+------------------------+
                                ^
                                |
       +-------------------------------------------------+
       |                   Data Layer                    |
       |   (Room DB, Retrofit API, Repository Impl, DTOs) |
       +-------------------------------------------------+
```

* **Domain Layer:** Pure Kotlin library containing core business logic (Models, Use Cases, Repository Contracts). It remains completely isolated from UI framework details.
* **Data Layer:** Implements data synchronization, Room persistence, and remote API interactions. Maps DTOs and database Entities directly to Domain models.
* **Presentation Layer:** Manages state using standard Android ViewModels, ViewBinding, and StateFlow emissions to cleanly expose non-blocking state (`Loading`, `Success`, `Error`, `Empty`) to Fragments.

---

## 📶 2. Offline Support

Offline persistence is powered by **Room Database**.

* The application employs a **Cache-First / Stale-While-Revalidate** policy in `CourseRepositoryImpl`.
* When fetching courses, cached entities from Room are instantly loaded and displayed on the UI.
* Remote networks are queried concurrently. On success, local entities update; on offline or network errors, execution falls back gracefully to the cached data without throwing UI crashes.

---

## 🔒 3. Security

For production deployments, the project enforces the following security standards:

* **Token Storage:** Authentication tokens (JWTs/Bearer tokens) should be stored securely in `EncryptedSharedPreferences` backed by the Android KeyStore system (`MasterKeys` / `EncryptedFile`).
* **Certificate Pinning:** HTTPS Certificate Pinning (via OkHttp `CertificatePinner`) should be enabled to prevent Man-In-The-Middle (MITM) attacks.
* **Memory Management:** Sensitive fields in memory should be wiped immediately after usage, avoiding plain text logging.

---

## 🚀 4. Scale (1M Users + Hundreds of Courses)

To scale this application efficiently:

* **Paging 3 Integration:** Implement Room + Network pagination (`RemoteMediator`) to fetch and load courses in chunks rather than fetching complete lists.
* **WorkManager Sync Engine:** Implement a robust background synchronization engine using `WorkManager` to queue offline mutations (e.g., lesson completions) with an exponential backoff strategy when connectivity is restored.
* **Database Indexing:** Ensure Room schemas have indexes on foreign keys (`courseId`) and queried fields to keep lookup speeds $O(\log N)$ or $O(1)$.
* **Image/Asset Caching:** Integrate Coil or Glide with memory and disk cache policies for course thumbnails and media assets.

---

## 🍎 5. Second Platform (iOS / macOS Strategy)

To port this app cleanly to iOS/macOS using Swift / SwiftUI:

* **Architecture:** Adopt Clean Architecture + MVVM using `ObservableObject` or Swift's `@Observable` macro.
* **UI Layer:** Replace XML/Fragments with native SwiftUI Views (`NavigationStack`, `List`, `ProgressView`).
* **Data Layer:** Replace Room with SwiftData or Core Data for database persistence.
* **Network Layer:** Use `URLSession` with `async`/`await` and native `Codable` protocols replacing DTOs.
* **Concurrency:** Utilize Swift Structured Concurrency (`Task`, `AsyncStream`) mirroring Kotlin Coroutines and `Flow`.
