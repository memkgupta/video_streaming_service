from dataclasses import dataclass, field
from enum import Enum
from typing import Optional
from datetime import datetime

# Reuse your earlier DTO
from dto.moderation_result import ModerationResult


class UpdateType(Enum):

    MODERATION_UPDATE = "MODERATION_UPDATE"
    TRANSCRIPT_UPDATE = "TRANSCRIPT_UPDATE"

from dataclasses import dataclass, field
from typing import Optional
from datetime import datetime
import json

@dataclass
class UpdateRequestDTO:
    videoId: str
    timestamp: str = field(
        default_factory=lambda: datetime.utcnow().isoformat()
    )
    type: Optional[UpdateType] = None
    moderation_result: Optional[ModerationResult] = None

    def to_dict(self):
        return {
            "videoId": self.videoId,
            "timestamp": self.timestamp,
            "type": self.type,
            "moderationResult": (
                self.moderation_result.to_dict()
                if self.moderation_result else None
            )
        }

    def to_json(self) -> str:
        return json.dumps(self.to_dict())
