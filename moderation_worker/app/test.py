import subprocess
import json
import os

from pipeline.pipeline import ModerationPipeline
from dtos.dtos import ContentType

# ───────────────────────────────────────────────────────────────
# 🎬 Get Video Duration (using ffprobe)
# ───────────────────────────────────────────────────────────────




# ───────────────────────────────────────────────────────────────
# 🧪 Test Runner
# ───────────────────────────────────────────────────────────────

def test_pipeline(video_path: str):
    if not os.path.exists(video_path):
        raise FileNotFoundError(f"Video not found: {video_path}")

    print("\n📂 Using video:", video_path)

    # Step 1 → Init pipeline
    pipeline = ModerationPipeline()

    # Step 2 → Run pipeline
    result = pipeline.run(
        video_id="test_video",
        video_url=video_path,  # local path works with ffmpeg
        job_id="123",
        asset_id="123",
        content_type=ContentType.VIDEO
    )

    # Step 3 → Pretty print result
    print("\n📊 FINAL RESULT:\n")

    print(result.model_dump_json())
    print("KAFKA EVENT \n\n")
    print(result.to_kafka_event())


# ───────────────────────────────────────────────────────────────
# ▶️ Entry Point
# ───────────────────────────────────────────────────────────────

if __name__ == "__main__":
    # 🔥 Put your local video path here
    VIDEO_PATH = "sample.mp4"

    test_pipeline(VIDEO_PATH)