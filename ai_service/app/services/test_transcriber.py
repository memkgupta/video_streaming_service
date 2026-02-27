import logging
from app.services.transcriber import transcribe_video

# Basic logging setup for test
logging.basicConfig(level=logging.INFO)

def test_transcription():
    job = {
        "videoId": "sample-test-001",
        "videoPath": "/home/mayank-gupta/Downloads/video.mp4"
    }

    print("Starting transcription test...")

    transcript = transcribe_video(job)

    print("\n====== TRANSCRIPT OUTPUT ======")
    print(transcript)
    print("====== END ======")


if __name__ == "__main__":
    test_transcription()
