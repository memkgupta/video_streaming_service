from pydantic import BaseModel
from typing import List


class ModerationJob(BaseModel):
    jobId: str
    url: str
    size: int
  
