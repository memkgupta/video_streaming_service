from pydantic import BaseModel
from typing import List, Optional


class TranscriptSegment(BaseModel):
    start: float
    end: float
    text: str

class TranscriptEvent(BaseModel):
    asset_id: str
    media_id: str
    chunk_number: int
    start_time: float
    end_time: float
    transcript: str