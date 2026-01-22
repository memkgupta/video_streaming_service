# app/models/ocr_easy.py
import cv2
import easyocr
from app.models.base import Detector

class OCRDetector(Detector):

    def __init__(self):
        self.reader = easyocr.Reader(['en'])

    def detect(self, frame_path: str) -> dict:
        img = cv2.imread(frame_path)
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
        results = self.reader.readtext(gray)

        text = [r[1] for r in results if r[2] > 0.5]
        return {"ocr_text": text}
