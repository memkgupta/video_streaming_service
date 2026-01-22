from dataclasses import dataclass, field
from enum import Enum
from typing import Optional
from datetime import datetime

# Reuse your earlier DTO
from app.dto.moderation_result import ModerationResult


class UpdateType(Enum):
    UPLOAD = "UPLOAD"
    TRANSCODE = "TRANSCODE"
    MODERATION = "MODERATION"
    TRANSCRIPT = "TRANSCRIPT"


@dataclass
class UpdateRequestDTO:
    video_id: str
    

    timestamp: str = field(
        default_factory=lambda: datetime.utcnow().isoformat()
    )
   
    type: Optional[UpdateType] = None
    
    moderation_result: Optional[ModerationResult] = None

    def __str__(self) -> str:
        return (
            f"UpdateRequestDTO(video_id='{self.video_id}', "
           
            f"timestamp='{self.timestamp}', "
           
        )
