from dataclasses import dataclass, field
from enum import Enum
from typing import List, Dict, Any,Optional
from enum import Enum
from typing import Optional
from enum import Enum
import json
class ModerationStatus(str, Enum):
    SAFE = "SAFE"
    REVIEW = "REVIEW"
    BLOCKED = "BLOCKED"


class ModerationFlag(str, Enum):
    NSFW = "NSFW"
    WEAPON = "WEAPON"
    OCR = "DRUGS"
    VIOLENCE = "VIOLENCE"

    @classmethod
    def from_str(cls, value: str):
        if not value:
            return None
        return cls.__members__.get(value.upper())



@dataclass
class ModerationResult:
    id: str
    status: ModerationStatus
    confidence_score: float
    flags: List[ModerationFlag] = field(default_factory=list)
    metadata: Dict[str, Any] = field(default_factory=dict)
    video_id: str = ""

    def to_dict(self) -> Dict[str, Any]:
        return {
            "id": self.id,
            "status": self.status, 
            "confidenceScore": self.confidence_score,
            "flags": [flag for flag in self.flags],
            "metadata": self._sanitize_metadata(self.metadata),
            "videoId": self.video_id,
        }

    def to_json(self) -> str:
        return json.dumps(self.to_dict())

    @staticmethod
    def _sanitize_metadata(metadata: Dict[str, Any]) -> Dict[str, Any]:
        """
        Ensures metadata is JSON-safe.
        """
        safe = {}
        for k, v in metadata.items():
            if isinstance(v, Enum):
                safe[k] = v.value
            elif isinstance(v, (str, int, float, bool)) or v is None:
                safe[k] = v
            else:
                safe[k] = str(v)  # last-resort safety
        return safe