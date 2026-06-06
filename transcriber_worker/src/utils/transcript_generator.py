from faster_whisper.transcribe import Segment
from src.models.transcript import TranscriptEvent, TranscriptSegment
from src.models.job import TranscriptionJob
class TranscriptEventFactory:

    @staticmethod
    def create(
        job: TranscriptionJob,
        whisper_segments: list[Segment],
        transcript_url: str | None = None
    ) -> TranscriptEvent:

        transcript_parts = []

        for segment in whisper_segments:
            text = segment.text.strip()
            transcript_parts.append(text)

        transcript = " ".join(transcript_parts)
        return TranscriptEvent(
            asset_id=job.asset_id,
            media_id=job.media_id,
            chunk_number=job.chunk_number,
            start_time=job.start_time,
            end_time=job.end_time,
            transcript=transcript
        )