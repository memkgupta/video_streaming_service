# app/pipeline/extractor.py
import subprocess
import os

def extract_frames(video_url: str, out_dir: str, fps=1):
    os.makedirs(out_dir, exist_ok=True)

    subprocess.run([
        "ffmpeg", "-y",
        "-i", video_url,
        "-vf", f"fps={fps}",
        f"{out_dir}/frame_%05d.jpg"
    ], check=True)
