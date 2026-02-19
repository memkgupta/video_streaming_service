# app/models/nsfw_clip.py
import torch, clip
from PIL import Image
from models.base import Detector

class ClipNSFWDetector(Detector):

    def __init__(self, device="cpu"):
        self.device = device
        self.model, self.preprocess = clip.load("ViT-B/32", device=device)

        self.prompts = clip.tokenize([
            "pornographic sexual content",
            "safe normal photo"
        ]).to(device)

    def detect(self, frame_path: str) -> dict:
        image = self.preprocess(Image.open(frame_path)).unsqueeze(0).to(self.device)

        with torch.no_grad():
            img_f = self.model.encode_image(image)
            txt_f = self.model.encode_text(self.prompts)
            probs = (img_f @ txt_f.T).softmax(dim=-1)

        return {"nsfw_score": float(probs[0][0])}
