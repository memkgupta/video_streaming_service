import subprocess
from app.docker_spawner import DockerSpawner
from app.config import (
    WORKER_IMAGE,
    CALLBACK_URL,
    WHISPER_MODEL,
    TRANSCRIPT_S3_BUCKET as TRANSCRIPT_BUCKET,
    VIDEO_S3_BUCKET as AWS_TRANSCODED_BUCKET_NAME
)


class LocalDockerSpawner(DockerSpawner):

    def spawn(self, video_id: str, video_key: str):
        """
        Launch Whisper worker container dynamically
        """

        print(f"Spawning container for video: {video_id}")

        command = [
            "docker", "run", "--rm",

            "-e", f"VIDEO_PATH={video_key}",
            "-e", f"VIDEO_ID={video_id}",
            "-e", f"AWS_TRANSCODED_BUCKET_NAME={AWS_TRANSCODED_BUCKET_NAME}",
            "-e", f"TRANSCRIPT_BUCKET={TRANSCRIPT_BUCKET}",
            "-e", f"WHISPER_MODEL={WHISPER_MODEL}",
            "-e", f"CALLBACK_URL={CALLBACK_URL}",

            WORKER_IMAGE
        ]

        subprocess.Popen(command)
