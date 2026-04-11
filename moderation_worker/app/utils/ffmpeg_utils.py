import subprocess
import os
import tempfile
def extract_chunk( video_url: str, start: float, end: float) -> str:
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4")
        output_path = temp_file.name
        temp_file.close()

        cmd = [
            "ffmpeg",
            "-loglevel", "quiet",
            "-ss", str(start),
            "-to", str(end),
            "-i", video_url,
            "-c", "copy",
            "-y",
            output_path,
        ]

        result = subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

        if result.returncode != 0:
            raise RuntimeError("FFmpeg extraction failed")

        return output_path

def extract_frames(chunk_path, frames_dir):
    os.makedirs(frames_dir, exist_ok=True)

    cmd = [
        "ffmpeg",
        "-loglevel", "quiet",
        "-i", chunk_path,
        "-vf", "fps=1",
        f"{frames_dir}/frame_%04d.jpg"
    ]

    subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

def extract_audio(chunk_path, audio_path):
    cmd = [
        "ffmpeg",
        "-loglevel", "quiet",
        "-i", chunk_path,
        "-q:a", "0",
        "-map", "a",
        audio_path
    ]

    subprocess.run(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)

import json
def get_video_length(video_path: str) -> float:
    cmd = [
        "ffprobe",
        "-v", "error",
        "-show_entries", "format=duration",
        "-of", "json",
        video_path,
    ]

    result = subprocess.run(cmd, capture_output=True, text=True)

    if result.returncode != 0:
        raise RuntimeError("Failed to get video length")

    data = json.loads(result.stdout)
    return float(data["format"]["duration"])
