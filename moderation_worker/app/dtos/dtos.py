"""
Pydantic schemas for:
  • ModerationJob    – inbound message from RabbitMQ
  • ChunkResult      – result from one text/media chunk
  • ModerationFlag   – a single detected category flag
  • ModerationResult – aggregated final result (persisted + published)
"""
from __future__ import annotations
from dtos.chunk_result import ChunkResult
import uuid
from datetime import datetime, timezone
from enum import Enum
from typing import Any

from pydantic import BaseModel, Field, model_validator


# ── Enumerations ─────────────────────────────────────────────────────────────

class ContentType(str, Enum):
    TEXT = "text"
    IMAGE = "image"
    VIDEO = "video"

class Priority(str, Enum):
    LOW = "low"
    NORMAL = "normal"
    HIGH = "high"

class ModerationCategory(str, Enum):
    NSFW = "nsfw"
    HATE = "hate"
    VIOLENCE = "violence"
    SPAM = "spam"
    PII = "pii"

class ModerationStatus(str, Enum):
    APPROVED = "approved"   # No flags
    FLAGGED = "flagged"     # Score above flag threshold
    REJECTED = "rejected"   # Score above reject threshold


class ModerationJob(BaseModel):
    """Message consumed from RabbitMQ."""
    job_id: str 
    asset_id: str 
    content_type: ContentType = ContentType.VIDEO
    # Either inline content or a URL (one must be provided for text/image)
    content: str | None = None
    content_url: str | None = None
    metadata: dict[str, Any] = Field(default_factory=dict)
    checks: list[ModerationCategory] = Field(
        default_factory=lambda: list(ModerationCategory)
    )
    priority: Priority = Priority.NORMAL
    attempt: int = 0
    @model_validator(mode="after")
    def content_or_url(self) -> "ModerationJob":
        if self.content is None and self.content_url is None:
            raise ValueError("Either 'content' or 'content_url' must be provided.")
        return self
# ── Per-chunk result
class ModerationFlag(BaseModel):
    category: ModerationCategory
    score: float = Field(..., ge=0.0, le=1.0)
    evidence: str | None = None



# ── Aggregated Result
class ModerationResult(BaseModel):
    """Final result – persisted to DB and published to Kafka."""
    job_id: str
    asset_id: str
    content_type: ContentType
    status: ModerationStatus
    overall_score: float = Field(..., ge=0.0, le=1.0)
    flags: list[ModerationFlag] = Field(default_factory=list)
    chunk_results: list[ChunkResult] = Field(default_factory=list)
    chunks_total: int = 0
    chunks_flagged: int = 0
    violation_count: int = 0
    processing_ms: int = 0
    metadata: dict[str, Any] = Field(default_factory=dict)
    completed_at: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    
    def to_kafka_event(self) -> dict[str, Any]:
        """Serialise to the ModerationUpdateDTO structure for Kafka."""
        return {
            "moderationStatus": self.status.upper(), # Assuming Java enum expects uppercase
            "moderationResult": self.model_dump(mode="json", exclude={"chunk_results"}),
            "assetId": self.asset_id,
            "jobId":self.job_id,
            "violationCount": self.violation_count
        }