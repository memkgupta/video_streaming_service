import os
import requests
import whisper
import subprocess
import boto3
import os
from pathlib import Path
S3_BUCKET = os.getenv("AWS_TRANSCODED_BUCKET_NAME")
WHISPER_MODEL = os.getenv("WHISPER_MODEL")
TRANSCRIPT_BUCKET = os.getenv("TRANSCRIPT_BUCKET")
VIDEO_KEY = os.getenv("VIDEO_KEY")
s3 = boto3.client("s3")

def download_from_s3(key: str, download_dir="/tmp") -> str:
    """
    Downloads file from S3 and returns local file path
    """

    filename = os.path.basename(key)
    local_path = os.path.join(download_dir, filename)

    print(f"Downloading from S3: {S3_BUCKET}/{key}")

    s3.download_file(S3_BUCKET, key, local_path)

    print(f"Saved to: {local_path}")

    return local_path

def upload_to_s3(local_file: str, bucket: str, key: str):
    """
    Upload file to S3
    """
    print(f"Uploading transcript to s3://{bucket}/{key}")
    s3.upload_file(local_file, bucket, key)


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



VIDEO_ID = os.getenv("VIDEO_ID")
CALLBACK_URL = os.getenv("CALLBACK_URL")

if  not VIDEO_ID or not CALLBACK_URL:
    raise RuntimeError("Missing required environment variables")

print("Loading Whisper model...")
VIDEO_PATH = download_from_s3(VIDEO_KEY,"/tmp")
audio_path = f"/tmp/{VIDEO_ID}.wav"



transcript = transcribe_in_chunks(VIDEO_PATH,VIDEO_ID)

local_transcript = f"/tmp/{VIDEO_ID}.txt"
with open(local_transcript, "w", encoding="utf-8") as f:
        f.write(transcript)

    # Upload back to S3
        upload_to_s3(
        local_transcript,
        TRANSCRIPT_BUCKET,
        f"transcripts/{VIDEO_ID}.txt"
        )

        print("Sending transcript back to main service...")
        requests.post(CALLBACK_URL, json={
         "videoId": VIDEO_ID,
         "transcriptURL": transcript
         })

print("Done transcription inside container")
