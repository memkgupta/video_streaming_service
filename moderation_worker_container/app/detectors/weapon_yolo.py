# app/models/weapon_yolo.py
from ultralytics import YOLO
from app.models.base import Detector

class WeaponDetector(Detector):

    def __init__(self):
        self.model = YOLO("yolov8n.pt")

    def detect(self, frame_path: str) -> dict:
        results = self.model(frame_path, verbose=False)[0]
        weapons = []

        for cls in results.boxes.cls:
            label = self.model.names[int(cls)]
            if label in ["knife", "gun"]:
                weapons.append(label)

        return {"weapons": weapons}
