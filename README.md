# Lumina AI - Generator Obrazów AI na Androida

**Lumina AI** to nowoczesna aplikacja na platformę Android stworzona w języku **Kotlin** z wykorzystaniem **Jetpack Compose** oraz **Material Design 3**. Aplikacja umożliwia generowanie wysokiej jakości grafik cyfrowych przy użyciu sztucznej inteligencji, automatyczne wzbogacanie promptów, organizację własnej kolekcji w lokalnej bazie danych oraz bezpośredni eksport obrazów do systemowej galerii telefonu.

---

## 🚀 Główne Funkcjonalności

- **Hybrydowe Silniki AI**:
  - **Silnik Cloud Flux (Darmowy)**: Działa bez limitów, natychmiast generując fotorealistyczne grafiki i ilustracje bez konieczności podpinania karty płatniczej.
  - **Google Gemini Pro (`gemini-3-pro-image`)**: Oficjalny model od Google zintegrowany przez **Firebase AI Logic**, wymagający planu Blaze w Firebase.
- **Asystent Promptów (AI Prompt Enhancer)**:
  - Przycisk *"Ulepsz prompt (AI)"* wykorzystuje model Gemini Flash do rozwinięcia prostego pomysłu w szczegółowy, profesjonalny opis kompozycji, oświetlenia i stylu.
- **Wybór Rozdzielczości i Formatów**:
  - Dostępne jakości: **1K** (1024px), **2K** (2048px) oraz **4K** (4096px).
  - Obsługiwane proporcje kadru: **1:1** (Kwadrat), **16:9** (Panorama), **9:16** (Pion / Stories), **4:3**, **3:4**.
- **Presety Stylów Artystycznych**:
  - Gotowe profile: *Naturalny*, *Cyberpunk*, *Fotorealizm*, *Anime*, *Dark Fantasy*, *Synthwave*, *3D Pixar/Render*.
- **Zapis do Galerii Telefonu**:
  - Pełna integracja z Android **MediaStore API** (`MediaStore.Images.Media.EXTERNAL_CONTENT_URI`).
  - Zgodność z mechanizmem *Scoped Storage* – nie wymaga przestarzałych, inwazyjnych uprawnień `WRITE_EXTERNAL_STORAGE` na Android 10+.
- **Wbudowana Galeria & Ulubione**:
  - Baza danych **Room** przechowująca historię wygenerowanych prac w pamięci prywatnej aplikacji.
  - Możliwość oznaczania grafik jako **Ulubione** i filtrowania kolekcji.
  - Pełnoekranowy podgląd szczegółów (prompt, data, styl, rozdzielczość).
  - Udostępnianie grafik do innych aplikacji poprzez bezpieczny `FileProvider`.
- **Nowoczesny Interfejs (Dark Neon Theme)**:
  - Głęboka czerń studyjna z neonowymi akcentami (Electric Cyan, Cyber Violet, Neon Pink).
  - Płynne animacje, stany ładowania (shimmer) oraz obsługa gestu wstecz (`BackHandler`).

---

## 🏛️ Architektura i SOLID

Projekt został zaprojektowany z rygorystycznym podziałem odpowiedzialności (**Clean Architecture** / **MVVM**):

```
app/src/main/java/com/example/
├── core/                        # Konfiguracja App Check (Firebase)
├── domain/                      # Czysta warstwa logiki biznesowej
│   ├── model/                   # Modele danych: GeneratedImageItem, ArtStyle, AiEngine itp.
│   └── repository/              # Interfejsy: IAiImageGenerator, IPromptEnhancer, IGalleryRepository
├── data/                        # Warstwa danych i integracji
│   ├── datasource/
│   │   ├── dao/                 # GeneratedImageDao (Room)
│   │   ├── database/            # AppDatabase (Room)
│   │   ├── local/               # ImageFileStorage, ApiKeyPreferences
│   │   └── mediastore/          # MediaStoreExporter (eksport do galerii)
│   ├── service/                 # GeminiImageGenerator, CloudFluxImageGenerator, CompositeImageGenerator
│   └── repository/              # GalleryRepositoryImpl
├── ui/                          # Warstwa prezentacji (Jetpack Compose)
│   ├── theme/                   # Paleta kolorów, motyw Neon Dark i typografia
│   ├── navigation/              # Ekrany i nawigacja dolnego paska (Studio, Galeria)
│   ├── studio/                  # Ekran tworzenia obrazów i komponenty formularza
│   └── gallery/                 # Ekran kolekcji, kafelki miniatur i podgląd pełnoekranowy
└── MainActivity.kt              # Punkt wejścia aplikacji, wstrzykiwanie zależności
```

### Zastosowane zasady SOLID:
1. **Single Responsibility Principle (SRP)**: Każda klasa odpowiada za jedno zadanie (np. `MediaStoreExporter` odpowiada wyłącznie za systemowy zapis do galerii, a `CloudFluxImageGenerator` za komunikację z silnikiem obrazów).
2. **Open/Closed Principle (OCP)**: Nowe silniki generowania grafik implementują interfejs `IAiImageGenerator` bez konieczności modyfikowania widoków ani ViewModeli.
3. **Liskov Substitution Principle (LSP)**: Zarówno `GeminiImageGenerator`, jak i `CloudFluxImageGenerator` mogą być zamiennie używane przez `CompositeImageGenerator`.
4. **Interface Segregation Principle (ISP)**: Interfejsy są wąskie i sfokusowane (`IPromptEnhancer`, `IMediaStoreExporter`, `IAiImageGenerator`).
5. **Dependency Inversion Principle (DIP)**: `StudioViewModel` oraz `GalleryViewModel` zależą wyłącznie od interfejsów repozytoriów wstrzykiwanych w konstruktorze.

---

## 🛠️ Wymagania i Technologie

- **System operacyjny**: Android 7.0 (API poziom 24) lub nowszy (Target SDK: 36).
- **Język**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose (BOM 2024.09.00) + Material 3.
- **Baza danych**: Room Database 2.7.0 z procesorem KSP.
- **Biblioteki sieciowe**: OkHttp 4.10.0, Coil Compose 2.7.0.
- **AI & Backend**: Firebase AI Logic (`firebase-ai`), Firebase App Check (reCAPTCHA Enterprise & Debug).

---

## ⚙️ Konfiguracja i Uruchomienie

### 1. Pobranie i kompilacja
Aplikację można otworzyć w środowisku **Android Studio**:
```bash
git clone <repo-url>
cd <repo-folder>
gradle assembleDebug
```

### 2. Konfiguracja Firebase AI Logic (opcjonalnie dla Gemini Pro)
- Plik `google-services.json` oraz `firebase_applet_config.xml` są automatycznie połączone z aplikacją.
- Aby korzystać z modelu `gemini-3-pro-image`, upewnij się, że projekt w [Konsoli Firebase](https://console.firebase.google.com/) posiada włączony plan **Blaze** (Pay-as-you-go).
- Bez planu Blaze aplikacja automatycznie korzysta z wbudowanego **Silnika Cloud Flux**, który nie wymaga żadnej dodatkowej konfiguracji.

---

## 🧪 Testy

Aplikacja zawiera zestaw testów jednostkowych oraz testów Robolectric weryfikujących logikę i zasoby aplikacji:
```bash
gradle :app:testDebugUnitTest
```

---

## 📄 Licencja

Projekt stworzony na platformie **Google AI Studio**.
