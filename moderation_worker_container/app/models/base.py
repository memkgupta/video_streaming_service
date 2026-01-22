from abc import ABC , abstractmethod

class Detector(ABC):
    @abstractmethod
    def detect(self,frame_path:str)->dict:
        pass