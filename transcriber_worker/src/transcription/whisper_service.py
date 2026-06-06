from faster_whisper import WhisperModel


class WhisperService:

    def __init__(self):
        self.model = WhisperModel(
            "small",
            device="cpu",
            compute_type="float32"
        )

    def transcribe(self, audio_path: str):

        segments, info = self.model.transcribe(
            audio_path,
            word_timestamps=True
        )

        return list(segments)