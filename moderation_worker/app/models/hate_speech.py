import os
import tempfile
import subprocess
from detoxify import Detoxify
import torch
import whisper
from deep_translator import GoogleTranslator



class AudioExtractor:
    def extract_audio(self, video_path: str) -> str:
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".wav")
        audio_path = temp_file.name
        temp_file.close()

        cmd = [
            "ffmpeg",
            "-loglevel", "quiet",
            "-i", video_path,
            "-vn",
          
            "-ar", "16000",
            "-ac", "1",
            "-y",
            audio_path,
        ]

        result = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

        if result.returncode != 0 or not os.path.exists(audio_path) or os.path.getsize(audio_path) < 100:
            return None

        return audio_path

class SpeechToText:
    def __init__(self, model_size: str = "base"):
        self.model = whisper.load_model(model_size)

    def transcribe(self, audio_path: str) -> str:
        try:
            result = self.model.transcribe(audio_path)
            return result.get("text", "").strip()
        except Exception:
            return ""
class HateSpeechClassifier:
    def __init__(self, device: str = "cpu"):
        # ML model
        self.model = Detoxify("original", device=device)

        # Translator
        self.translator = GoogleTranslator(source="auto", target="en")


        slangs_file_path = os.path.join(os.path.dirname(__file__), "hindi_slangs.txt")
        try:
            with open(slangs_file_path, "r", encoding="utf-8") as f:
                self.hindi_slang = {line.strip() for line in f if line.strip()}
        except Exception:
            self.hindi_slang = set()


    def _slang_score(self, text: str) -> float:
        text_lower = text.lower()

        for word in self.hindi_slang:
            if word in text_lower:
                return 0.85  # strong signal

        return 0.0


    def _translate(self, text: str) -> str:
        try:
            return self.translator.translate(text)
        except Exception:
            return text  # fallback

    def predict(self, text: str) -> float:
        if not text or not text.strip():
            return 0.0

        # 1. Slang signal (original text)
        slang_score = self._slang_score(text)

        # 2. Translate to English
        translated_text = self._translate(text)

        # 3. Detoxify prediction
        result = self.model.predict(translated_text)

        detox_score = max(
            result.get("toxicity", 0.0),
            result.get("severe_toxicity", 0.0),
            result.get("identity_attack", 0.0),
            result.get("threat", 0.0),
        )

        # 4. Final score (combine signals)
        final_score = max(detox_score, slang_score)

        return float(final_score)
class HateSpeechModel:
    def __init__(self):
        self.audio_extractor = AudioExtractor()
        self.stt = SpeechToText(model_size="base")
        self.classifier = HateSpeechClassifier()

    def predict(self, video_path: str) -> float:
        audio_path = None

        try:
            # 1. Extract audio
            audio_path = self.audio_extractor.extract_audio(video_path)
            if not audio_path:
             return 0.0
            # 2. Speech to text
            text = self.stt.transcribe(audio_path)

            if not text:
                return 0.0

            # 3. Classify
            score = self.classifier.predict(text)

            return score

        finally:
            if audio_path and os.path.exists(audio_path):
                os.remove(audio_path)