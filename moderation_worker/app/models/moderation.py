from models.nsfw_model import NSFWModel
from models.violence_model import ViolenceModel
from models.hate_speech import HateSpeechModel
class ModerationModels:
    def nsfw(self, video_path: str) -> float:
        raise NotImplementedError

    def violence(self, video_path: str) -> float:
        raise NotImplementedError

    def hate_speech(self, video_path: str) -> float:
        raise NotImplementedError



class DefaultModerationModels(ModerationModels):
    def __init__(self):
        self.nsfw_model = NSFWModel(fps=1)
        self.violence_model = ViolenceModel(fps=2)
        self.hate_model = HateSpeechModel()

    def nsfw(self, video_path: str) -> float:
        return self.nsfw_model.predict(video_path)

    def violence(self, video_path: str) -> float:
        return self.violence_model.predict(video_path)

    def hate_speech(self, video_path: str) -> float:
        return self.hate_model.predict(video_path)