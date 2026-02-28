import boto3
import os
from urllib.parse import urlparse
from app.config import S3_BUCKET
s3 = boto3.client("s3")

def download_from_s3(key: str, download_dir="/tmp") -> str:
    """
    Downloads file from S3 and returns local file path
    """

    filename = os.path.basename(key)
    local_path = os.path.join(download_dir, filename)

    print(f"Downloading from S3: {S3_BUCKET}/{key}")

    s3.download_file(S3_BUCKET, key, local_path)

    print(f"Saved to: {local_path}")

    return local_path

def upload_to_s3(local_file: str, bucket: str, key: str):
    """
    Upload file to S3
    """
    print(f"Uploading transcript to s3://{bucket}/{key}")
    s3.upload_file(local_file, bucket, key)