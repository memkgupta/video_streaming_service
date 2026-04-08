from dataclasses import dataclass
from typing import List, Optional
import math
from utils.ffmpeg_utils import get_video_length


@dataclass
class VideoChunk:
    chunk_id: int
    start_time: float
    end_time: float
    length: float


class VirtualChunker:
    def __init__(
        self,
        chunk_length: float = 10.0,
        overlap: float = 0.0,
        min_chunk_length: float = 1.0,
    ):
        """
        :param chunk_length: length of each chunk in seconds
        :param overlap: overlap between chunks (seconds)
        :param min_chunk_length: minimum valid chunk length
        """
        assert chunk_length > 0, "chunk_length must be > 0"
        assert overlap >= 0, "overlap must be >= 0"
        assert overlap < chunk_length, "overlap must be less than chunk_length"

        self.chunk_length = chunk_length
        self.overlap = overlap
        self.min_chunk_length = min_chunk_length

    def generate_chunks(self, video_url: str) -> List[VideoChunk]:
        """
        Generate virtual chunks based on video length.

        :param video_url: URL of the video
        :return: list of VideoChunk
        """
        try:
            video_length = get_video_length(video_url)
        except Exception as e:
            print(f"Error fetching video length: {e}")
            video_length = 0

        if video_length <= 0:
            return []

        chunks: List[VideoChunk] = []
        step = self.chunk_length - self.overlap

        total_chunks = math.ceil(video_length / step)

        for i in range(total_chunks):
            start = i * step
            end = start + self.chunk_length

            # Clamp to video length
            if end > video_length:
                end = video_length

            length = end - start

            if length < self.min_chunk_length:
                continue

            chunks.append(
                VideoChunk(
                    chunk_id=i,
                    start_time=round(start, 3),
                    end_time=round(end, 3),
                    length=round(length, 3),
                )
            )

        return chunks