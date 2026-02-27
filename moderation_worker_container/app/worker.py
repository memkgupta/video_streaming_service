# app/worker.py this will be later converted to a docker container code
from pipeline.extractor import extract_frames
from detectors.nsfw_clip import ClipNSFWDetector
from detectors.weapon_yolo import WeaponDetector
from detectors.ocr_easy import OCRDetector
from pipeline.aggregator import aggregate_frame_results
from pipeline.decision import decide
from repository.moderation_repo import ModerationRepository
from producer import send_moderation_event
from utils.url import get_video_url_presigned
import os
from dto.summarisation_job import ModerationJob
def process_video(event:ModerationJob):
    video_id = event.jobId
    video_url = event.url

    frames_dir = f"/tmp/{video_id}/frames"
    extract_frames(video_url, frames_dir)

    detectors = [
        ClipNSFWDetector(),
        WeaponDetector(),
        OCRDetector()
    ]

    frame_results = []

    for frame in sorted(os.listdir(frames_dir)):
        path = os.path.join(frames_dir, frame)
        result = {}

        for d in detectors:
            result.update(d.detect(path))

        frame_results.append(result)

    summary = aggregate_frame_results(frame_results)
    decision = decide(summary)
    flags = []
    if summary.get("max_nsfw", 0) > 0.6:
        flags.append("NSFW")

    if summary.get("weapons"):
        flags.append("WEAPON")

    if summary.get("violence"):
        flags.append("VIOLENCE")
    report = {
        "video_id": video_id,
        "metadata": summary,
        "status": decision,
        "confidence_score":summary["max_nsfw"],
       
        "flags":flags,
    }
    print(report)
    repo = ModerationRepository()
    
    moderation_result = repo.save(report,video_id=video_id)
    # save the moderation report to a storage with the same
    send_moderation_event(video_id,moderation_result)
