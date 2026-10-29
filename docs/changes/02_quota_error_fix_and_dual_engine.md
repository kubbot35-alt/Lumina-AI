# Dokumentacja Zmian: Obsługa błędu Quota Exceeded (Limit 0) i Silnik Hybrydowy

## 1. Problem (Diagnoza błędu ze zrzutu ekranu)
Błąd: `Quota exceeded for metric: generativelanguage.googleapis.com/generate_content_free_tier_requests, limit: 0, model: gemini-3-pro-image`
- **Przyczyna**: Modele generowania obrazu Google Gemini (`gemini-3-pro-image`, `gemini-nano-banana-2.1`) w usłudze Firebase AI Logic oraz Google Cloud na darmowym planie (Spark / Free Tier) mają domyślnie odgórnie zdefiniowany limit równy **0 zapytań dziennie**. Wymagają one podpięcia karty / konta rozliczeniowego i włączenia planu Blaze w Firebase.

## 2. Rozwiązanie Architektoniczne
Zastosowano wzorzec Strategy oraz Composite (`CompositeImageGenerator` implementujący `IAiImageGenerator`):
- **Silnik Cloud Flux (`CloudFluxImageGenerator`)**:
  - W pełni funkcjonalny, darmowy silnik chmurowy AI (Flux/SDXL) działający bez limitu 0 zapytań i bez konieczności podawania karty kredytowej.
  - Zapewnia natychmiastowe generowanie prawdziwych grafik AI wysokiej jakości, które użytkownik może oglądać, zapisywać do galerii urządzenia (`MediaStore`) i dodawać do ulubionych.
- **Silnik Google Gemini Pro (`GeminiImageGenerator`)**:
  - Dostępny jako opcjonalny silnik dla użytkowników posiadających projekt z planem Blaze lub klucz z billingiem.
  - Wykrywa błędy limitu (limit: 0) i prezentuje czytelny, estetyczny komunikat w języku polskim z przyciskiem szybkiego wygenerowania obrazu za pomocą silnika Cloud Flux.
- **Przełącznik Silnika (`ConfigPickerSection` & `StudioViewModel`)**:
  - Użytkownik może w każdej chwili przełączać silnik generowania pomiędzy "Silnik AI Cloud (Darmowy)" a "Google Gemini Pro".
