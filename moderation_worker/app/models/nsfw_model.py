import os
import shutil
import tempfile
import subprocess
from typing import List
from nudenet import NudeDetector
import torch
import torchvision.transforms as transforms
from PIL import Image
from utils.frame_extractor import FrameExtractor

# ───────────────────────────────────────────────────────────────
# 🔧 Frame Extraction
# ───────────────────────────────────────────────────────────────




class NSFWModel:
    NSFW_LABELS = {
          "FEMALE_GENITALIA_COVERED",
    "FACE_FEMALE",
    "BUTTOCKS_EXPOSED",
    "FEMALE_BREAST_EXPOSED",
    "FEMALE_GENITALIA_EXPOSED",
    "MALE_BREAST_EXPOSED",
    "ANUS_EXPOSED",
    "FEET_EXPOSED",
    "BELLY_COVERED",
    "FEET_COVERED",
    "ARMPITS_COVERED",
    "ARMPITS_EXPOSED",
    "FACE_MALE",
    "BELLY_EXPOSED",
    "MALE_GENITALIA_EXPOSED",
    "ANUS_COVERED",
    "FEMALE_BREAST_COVERED",
    "BUTTOCKS_COVERED",
    }
     
    def __init__(self, device: str = "cpu", fps: int = 3):
        
        self.device = device
        self.frame_extractor = FrameExtractor(fps=fps)
        self.detector = NudeDetector()

    def predict(self, video_path: str) -> float:
        frames = []

        try:
            frames = self.frame_extractor.extract_frames(video_path)

            if not frames:
                return 0.0

            scores = []

            for frame in frames:
                detections = self.detector.detect(frame)

                frame_score = 0.0

                for obj in detections:
                  if obj["class"] in self.NSFW_LABELS:
                     frame_score = max(frame_score, obj["score"])

                scores.append(frame_score)

                # 🔥 Early exit
                if frame_score > 0.95:
                    return frame_score

            return max(scores) if scores else 0.0

        finally:
            self._cleanup(frames)

    def _cleanup(self, frames: List[str]):
        if not frames:
            return

        temp_dir = os.path.dirname(frames[0])

        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir)
  
        if not frames:
            return

        temp_dir = os.path.dirname(frames[0])

        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir)