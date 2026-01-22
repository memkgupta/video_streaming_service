from dataclasses import dataclass, field
from enum import Enum
from typing import List, Dict, Any,Optional
from enum import Enum
from typing import Optional

class ModerationStatus(Enum):
    SAFE = "SAFE"
    REVIEW = "REVIEW"
    BLOCKED = "BLOCKED"




class ModerationFlag(Enum):
    NSFW = "NSFW"
    WEAPON = "WEAPON"
    OCR = "DRUGS"
    VIOLENCE = "VIOLENCE"
    

    @classmethod
    def from_str(cls, value: str) -> Optional["ModerationFlag"]:
        if not value:
            return None
        return cls.__members__.get(value.upper())



@dataclass
class ModerationResult:
    id:str
   
    status: ModerationStatus
    confidence_score: float
    flags: List[str] = field(default_factory=list)
    metadata: Dict[str, Any] = field(default_factory=dict)
    video_id: str = ""
