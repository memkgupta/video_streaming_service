from fastapi import APIRouter
from app.kafka_producer import sendMessage
import logging

router = APIRouter()
logger = logging.getLogger("worker-callback")

@router.post("/transcript/callback")
async def transcript_callback(payload: dict):
    video_id = payload.get("videoId")
    transcript_url = payload.get("transcriptURL")

    if not video_id or not transcript_url:
        return {"status": "error", "message": "Missing required fields"}

    logger.info(f"Transcript received for video {video_id}")
    logger.info(f"Transcript stored at: {transcript_url}")
    sendMessage({
        "videoId":video_id,
        "transcriptURL":transcript_url
    })
    return {"status": "success"}
