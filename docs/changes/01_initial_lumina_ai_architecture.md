# Dokumentacja Architektury: Lumina AI

## 1. Wstęp
Aplikacja Lumina AI na system Android implementuje generowanie obrazów AI w oparciu o modele Gemini z wykorzystaniem Firebase AI Logic, asystenta ulepszania promptów, lokalną bazę Room oraz eksport do systemowej galerii Androida (`MediaStore`).

## 2. Podział Odpowiedzialności (SOLID & Clean Architecture)
- **Warstwa Domenowa (`domain`)**:
  - Czyste interfejsy biznesowe: `IAiImageGenerator`, `IPromptEnhancer`, `IGalleryRepository`, `IMediaStoreExporter`.
  - Niezmienne modele domenowe: `GeneratedImageItem`, `ArtStyle`, `ImageSizeOption`, `AspectRatioOption`.
  - Brak zależności od widoku (Jetpack Compose).
- **Warstwa Danych (`data`)**:
  - Implementacja repozytoriów: `GalleryRepositoryImpl`.
  - Integracja z Firebase AI Logic: `GeminiImageGenerator`, `GeminiPromptEnhancer`.
  - Dostęp do bazy danych: `GeneratedImageDao`, `AppDatabase`.
  - Zapis do pamięci wewnętrznej i galerii: `ImageFileStorage`, `MediaStoreExporter`.
- **Warstwa Prezentacji (`ui`)**:
  - `StudioViewModel`: Zarządzanie stanem tworzenia obrazu, komunikacja przez interfejsy.
  - `GalleryViewModel`: Reaktywne śledzenie kolekcji wygenerowanych obrazów przez `Flow<List<GeneratedImageItem>>`.
  - Komponenty Compose (SRP, krótkie metody, wsparcie dostępności i testów `testTag`).
