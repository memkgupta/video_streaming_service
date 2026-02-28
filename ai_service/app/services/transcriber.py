from app.services.chunk_transcriber import transcribe_in_chunks
from app.utils.s3_utils import download_from_s3,upload_to_s3
from app.config import TRANSCRIPT_BUCKET
from app.config import CDN
from app.kafka_producer import sendMessage
def transcribe_video(job: dict):
    video_id = job["jobId"]
    key = job["key"]

    video_path = download_from_s3(key,"/tmp")
    transcript_text = transcribe_in_chunks(video_path, video_id)

    # Save locally
    local_transcript = f"/tmp/{video_id}.json"
    with open(local_transcript, "w", encoding="utf-8") as f:
        f.write(transcript_text)

    # Upload back to S3
    upload_to_s3(
        local_transcript,
        TRANSCRIPT_BUCKET,
        f"transcripts/{video_id}.json"
    )
    message = {
        "transcriptURL":f"transcripts/{video_id}.json" , 
        "videoId":video_id
    }
    sendMessage(message)




