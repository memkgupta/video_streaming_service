import subprocess
import tempfile
import os


class VideoChunkExtractor:
    def extract(self, video_url: str, start: float, end: float) -> str:
        temp_file = tempfile.NamedTemporaryFile(delete=False, suffix=".mp4")
        output_path = temp_file.name
        temp_file.close()
        
        cmd = [
            "ffmpeg",
            "-loglevel", "error",
            "-ss", str(start),
            "-to", str(end),
            "-i", video_url,
            "-c", "copy",
            "-y",
            output_path,
        ]

        result = subprocess.run(cmd)

        if result.returncode != 0:
            raise RuntimeError("FFmpeg extraction failed")

        return output_path