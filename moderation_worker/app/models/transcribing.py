import asyncio
from typing import List, Optional
from faster_whisper import WhisperModel


class TranscriptionResult:
    def __init__(self, text: str, segments: List[dict], language: Optional[str]):
        self.text = text
        self.segments = segments
        self.language = language


class TranscriptionModel:
    def __init__(
        self,
        model_size: str = "base",
        device: str = "cpu",          # "cpu" or "cuda"
        compute_type: str = "int8",   # "int8", "float16", etc.
    ):
        self.model_size = model_size
        self.device = device
        self.compute_type = compute_type
        self._model: Optional[WhisperModel] = None

    def load(self):
        """
        Lazy load the model (important for worker startup performance)
        """
        if self._model is None:
            self._model = WhisperModel(
                self.model_size,
                device=self.device,
                compute_type=self.compute_type,
            )

    async def transcribe(
        self,
        audio_path: str,
        beam_size: int = 5,
    ) -> TranscriptionResult:
        """
        Async wrapper around blocking transcription
        """
        if self._model is None:
            self.load()

        loop = asyncio.get_event_loop()

        segments, info = await loop.run_in_executor(
            None,
            lambda: self._model.transcribe(
                audio_path,
                beam_size=beam_size,
            ),
        )

        collected_segments = []
        full_text = []

        for segment in segments:
            collected_segments.append({
                "start": segment.start,
                "end": segment.end,
                "text": segment.text,
            })
            full_text.append(segment.text)

        return TranscriptionResult(
            text=" ".join(full_text).strip(),
            segments=collected_segments,
            language=info.language if info else None,
        )

    async def health_check(self) -> bool:
        """
        Simple check to verify model is working
        """
        try:
            await self.transcribe("tests/assets/sample.wav")
            return True
        except Exception:
            return False