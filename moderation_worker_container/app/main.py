from app.worker import process_video
from app.dto.summarisation_job import ModerationJob
from app.config import JOB_ID,ASSET_URL,ASSET_SIZE
process_video(
    ModerationJob(
        jobId=JOB_ID,
        url=ASSET_URL,
        size=ASSET_SIZE
    )
)