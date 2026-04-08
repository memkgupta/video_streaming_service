import time
import os
from models.moderation import ModerationModels
from utils.ffmpeg_utils import extract_chunk
from dtos.chunk_result import ChunkResult

class ChunkWorker:
    def __init__(self, models: ModerationModels):
        self.models = models
        

    def process_chunk(
        self,
        video_url: str,
        chunk_id: int,
        start_time: float,
        end_time: float,
    ) -> ChunkResult:

        start_exec = time.time()
        video_path = None

        try:
            # 1. Extract chunk
            video_path = extract_chunk(
                video_url,
                start_time,
                end_time,
                
            )

            # 2. Run models
            nsfw_score = float(self.models.nsfw(video_path))
            violence_score = float(self.models.violence(video_path))
            hate_score = float(self.models.hate_speech(video_path))

            violation_count = sum(1 for s in (nsfw_score, violence_score, hate_score) if s > 0.8)

            return ChunkResult(
                chunk_id=chunk_id,
                start_time=start_time,
                end_time=end_time,
                nsfw_score=nsfw_score,
                violence_score=violence_score,
                hate_score=hate_score,
                violation_count=violation_count,
                processing_time=round(time.time() - start_exec, 3),
            )

        except Exception as e:
            import logging
            logging.error(f"Error processing chunk {chunk_id} ({start_time}-{end_time}): {e}", exc_info=True)
            return ChunkResult(
                chunk_id=chunk_id,
                start_time=start_time,
                end_time=end_time,
                nsfw_score=0.0,
                violence_score=0.0,
                hate_score=0.0,
                violation_count=0,
                processing_time=round(time.time() - start_exec, 3),
                error=str(e),
            )

        finally:
            # 3. Cleanup
            if video_path and os.path.exists(video_path):
                os.remove(video_path)