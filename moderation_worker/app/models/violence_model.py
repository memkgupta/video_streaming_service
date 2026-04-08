import os
import shutil
import tempfile
import subprocess
from typing import List
from utils.frame_extractor import FrameExtractor
import torch
import torchvision.transforms as transforms
from torchvision import models
from PIL import Image


# ───────────────────────────────────────────────────────────────
# 🎬 Frame Extraction (same as NSFW)
# ───────────────────────────────────────────────────────────────


# ───────────────────────────────────────────────────────────────
# 🤖 Violence Model
# ───────────────────────────────────────────────────────────────

class ViolenceModel:
    def __init__(self, device: str = "cpu", fps: int = 2):
        self.device = device
        self.frame_extractor = FrameExtractor(fps=fps)

        self.model = self._load_model()

        self.transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ])

    def _load_model(self):
        """
        Using pretrained ResNet (placeholder for violence detection)
        """
        model = models.resnet18(pretrained=True)

        # Replace final layer for binary classification
        model.fc = torch.nn.Sequential(
            torch.nn.Linear(model.fc.in_features, 1),
            torch.nn.Sigmoid()
        )

        model.eval()
        return model

    def _predict_frame(self, image_path: str) -> float:
        image = Image.open(image_path).convert("RGB")
        tensor = self.transform(image).unsqueeze(0)

        with torch.no_grad():
            output = self.model(tensor)

        return float(output.item())

    def predict(self, video_path: str) -> float:
        frames = []

        try:
            frames = self.frame_extractor.extract_frames(video_path)

            if not frames:
                return 0.0

            scores = []

            for frame in frames:
                score = self._predict_frame(frame)
                scores.append(score)

                # 🔥 Early exit (violence detected strongly)
                if score > 0.9:
                    return score

            # 🔥 Aggregation strategy
            return max(scores)

        finally:
            self._cleanup(frames)

    def _cleanup(self, frames: List[str]):
        if not frames:
            return

        temp_dir = os.path.dirname(frames[0])

        if os.path.exists(temp_dir):
            shutil.rmtree(temp_dir)