# Lumina AI - Generator Obrazów AI na Androida

Nowoczesna aplikacja Android w Jetpack Compose do generowania wysokiej jakości grafik AI z wykorzystaniem modeli Gemini (`gemini-3-pro-image-preview` / `gemini-nano-banana-2.1`), inteligentnym asystentem promptów, wyborem rozdzielczości (1K/2K/4K), presetami artystycznymi, natywnym zapisem do galerii urządzenia (MediaStore) oraz wbudowaną galerią historii z ulubionymi na bazie bazy Room.

## User Review & Critical Decisions

> [!IMPORTANT] Podsumowanie ustaleń z fazy pytań:
> - **Motyw i stylistyka**: Ciemny motyw neonowy (głęboka czerń z akcentami neon cyan, elektryczny fiolet i subtelny glow).
> - **Wspomaganie generowania**: Asystent ulepszania promptów AI (Gemini Flash/Argon) oraz siatka gotowych presetów stylistycznych (np. Cyberpunk, Fotorealizm, Anime, Dark Fantasy, Synthwave, Cinematic 3D).
> - **Zarządzanie grafikami**: Zapis do systemowej galerii zdjęć Androida (`MediaStore.Images.Media`), lokalna galeria w aplikacji (baza Room) z ulubionymi, filtrowaniem i podglądem pełnoekranowym oraz udostępnianiem przez FileProvider.
> - **Wybór jakości**: Selektor rozdzielczości (1K 1024px, 2K 2048px, 4K 4096px) oraz proporcji obrazu (1:1, 16:9, 9:16, 4:3).

---

## 1. Overview & Core Concept

- **Cel aplikacji**: Umożliwienie użytkownikom błyskawicznego tworzenia profesjonalnych obrazów AI z intuicyjnym interfejsem mobilnym, ulepszaniem krótkich pomysłów w bogate prompty oraz bezproblemowym eksportem i archiwizacją w pamięci telefonu.
- **Grupa docelowa**: Twórcy treści, projektanci, entuzjaści grafiki cyfrowej oraz użytkownicy poszukujący tapet i ilustracji.
- **Kluczowa wartość**: Generowanie obrazów w wysokiej rozdzielczości (aż do 4K), inteligentne podpowiedzi wzbogacające prompt jednym kliknięciem, bezstratny zapis do rolki aparatu w galerii Androida i natychmiastowy dostęp do historii prac offline.

---

## 2. User Experience & Visual Design

### Kluczowe ekrany i przepływy UX:
1. **Ekran Studio (Generator)**:
   - Pole tekstowe promptu z przyciskiem mikro-interakcji "Ulepsz z AI (Sparkle)".
   - Karuzela presetów stylów z ikonami i mini-tagami.
   - Selektor proporcji (1:1, 16:9, 9:16, 4:3, 3:4) oraz rozdzielczości (1K, 2K, 4K).
   - Główny przycisk akcji "Generuj Obraz" z animowanym stanem ładowania (shimmer, pulsujący neonowy wskaźnik postępu 30-60s).
   - Panel wyniku z podglądem, przyciskami "Zapisz w Galerii", "Ulubione" i "Udostępnij".
2. **Ekran Galeria (Kolekcja)**:
   - Dynamiczna siatka wygenerowanych obrazów (Staggered Grid) z oznaczeniem ulubionych.
   - Pasek filtrów: "Wszystkie", "Ulubione", "Ostatnie".
   - Stan pusty z przyjazną grafiką i zachętą do wygenerowania pierwszej grafiki.
3. **Ekran Podglądu Pełnoekranowego (Detail View)**:
   - Podgląd obrazu w wysokiej rozdzielczości z obsługą gestów.
   - Szczegóły generacji: użyty prompt, data, rozdzielczość, styl.
   - Szybkie akcje: Zapisz do galerii telefonu, Usuń z historii, Kopiuj prompt do edytora.

### Identyfikacja wizualna i motyw (Neon Dark):
- **Tło**: Głęboka czerń studyjna `#0A0E17` i `#111827`.
- **Akcenty**: Neon Cyan `#00F0FF`, Cyber Violet `#8A2BE2` oraz Electric Indigo `#6366F1`.
- **Typografia**: Nowoczesny krój bezszeryfowy o wysokiej czytelności i wyrazistych nagłówkach.
- **Karty i Powierzchnie**: Delikatne półprzezroczyste ramki z efektem szkła (Glassmorphism), subtelne obramowania neonowe przy aktywnych elementach.

---

## 3. Key Product Decisions & Trade-Offs

- **Wybór modelu generowania obrazu**:
  - *Decyzja*: Wykorzystanie `gemini-3-pro-image-preview` / `gemini-nano-banana-2.1` przez Firebase AI Logic z jawnym konfigurowaniem `ImageConfig` (proporcje i jakość 1K/2K/4K).
  - *Dlaczego*: Zapewnia najwyższą jakość generowanych grafik, zgodność z wymaganiami zadania oraz natywną integrację z Android SDK.
- **Asystent Promptów (Gemini 3.8 Flash / 4 Argon)**:
  - *Decyzja*: Przycisk "Rozwiń prompt z AI" wysyłający zwięzły pomysł użytkownika do modelu tekstowego, który zwraca profesjonalny, bogaty opis sceny, oświetlenia i detali.
  - *Dlaczego*: Znacznie poprawia jakość wygenerowanych obrazów i ułatwia korzystanie osobom niemającym doświadczenia z prompt engineeringiem.
- **Zapis do Galerii przez MediaStore**:
  - *Decyzja*: Zastosowanie Android `MediaStore.Images.Media` z `ContentResolver` i `IS_PENDING`.
  - *Dlaczego*: Jest to oficjalny, bezpieczny sposób zapisu obrazów do galerii systemowej dla Android 10+ (API 29-36), który nie wymaga niebezpiecznych uprawnień `WRITE_EXTERNAL_STORAGE`.
- **Przechowywanie lokalne (Room Database + Pamięć Wewnętrzna)**:
  - *Decyzja*: Zapis wygenerowanych plików PNG/JPEG w katalogu prywatnym aplikacji (`context.filesDir/generated_images`), a metadane (ścieżka, prompt, timestamp, rozdzielczość, ulubione) w Room Database.
  - *Dlaczego*: Zapewnia błyskawiczne wczytywanie miniatur, pełną dostępność historii offline oraz niezależność od zewnętrznych serwisów.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                   Jetpack Compose UI                   │
│   (StudioScreen, GalleryScreen, FullscreenViewer)      │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow / UI Events)
┌───────────────────────────┴────────────────────────────┐
│                    ViewModel Layer                     │
│    (ImageGenViewModel, GalleryViewModel, NavState)     │
└─────────────▲────────────────────────────▲─────────────┘
              │                            │
┌─────────────┴─────────────┐  ┌───────────┴─────────────┐
│    AiGenerationService    │  │    GalleryRepository    │
│  (Firebase AI / Gemini)   │  │ (Room DAO & File Storage│
│   - Image Synthesis       │  │  & MediaStore Saver)    │
│   - Prompt Enhancer       │  │                         │
└───────────────────────────┘  └─────────────────────────┘
```

### Moduły i Interfejsy:
1. `IAiImageGenerator`: Interfejs do generowania obrazów (`generateImage(prompt, size, aspectRatio) -> Bitmap`).
2. `IPromptEnhancer`: Interfejs do ulepszania promptów (`enhancePrompt(userPrompt, style) -> String`).
3. `IGalleryStorage`: Interfejs do zapisu w systemowym `MediaStore` oraz lokalnej bazie (`saveToDeviceGallery(bitmap, title)`, `saveToLocalHistory(...)`).
4. `GeneratedImageDao` & `AppDatabase`: Obsługa encji Room z polami: `id`, `filePath`, `prompt`, `enhancedPrompt`, `style`, `aspectRatio`, `imageSize`, `createdAt`, `isFavorite`.
