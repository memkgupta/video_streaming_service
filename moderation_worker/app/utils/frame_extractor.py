import tempfile
from typing import List
import os
from utils.ffmpeg_utils import extract_frames
class FrameExtractor:
    def __init__(self, fps: int = 1):
        self.fps = fps

    def extract_frames(self, video_path: str) -> List[str]:
        """
        Extract frames from video using ffmpeg
        """
        temp_dir = tempfile.mkdtemp()

        # output_pattern = os.path.join(temp_dir, "frame_%03d.jpg")
        extract_frames(video_path,temp_dir)
        frames = sorted(
            os.path.join(temp_dir, f)
            for f in os.listdir(temp_dir)
        )

        return frames
