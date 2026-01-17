import os
import subprocess
import whisper
from pathlib import Path
from app.config import WHISPER_MODEL

CHUNK_LENGTH = 30
CHUNK_DIR = "/tmp/chunks"


def extract_audio(video_path, audio_path):
    subprocess.run([
        "ffmpeg", "-y",
        "-i", video_path,
        "-vn",
        "-ac", "1",
        "-ar", "16000",
        audio_path
    ], check=True)


def split_audio(audio_path):
    os.makedirs(CHUNK_DIR, exist_ok=True)

    subprocess.run([
        "ffmpeg", "-y",
        "-i", audio_path,
        "-f", "segment",
        "-segment_time", str(CHUNK_LENGTH),
        "-c", "copy",
        f"{CHUNK_DIR}/chunk_%03d.wav"
    ], check=True)


def transcribe_in_chunks(video_path, video_id):
    audio_path = f"/tmp/{video_id}.wav"

    extract_audio(video_path, audio_path)
    split_audio(audio_path)

    model = whisper.load_model(WHISPER_MODEL)
    transcript = ""

    chunk_files = sorted(Path(CHUNK_DIR).glob("*.wav"))

    for idx, chunk in enumerate(chunk_files):
        result = model.transcribe(chunk, language="en", task="translate")

        for seg in result["segments"]:
            transcript += seg["text"]



    return transcript
