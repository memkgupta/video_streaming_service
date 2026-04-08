from dataclasses import dataclass


@dataclass
class ChunkResult:
    chunk_id: int
    start_time: float
    end_time: float

    nsfw_score: float
    violence_score: float
    hate_score: float

    violation_count: int
    processing_time: float
    error: str | None = None