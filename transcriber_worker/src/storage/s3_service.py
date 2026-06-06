import boto3
import json
import logging
import urllib.request
import shutil
from urllib.parse import urlparse

logger = logging.getLogger("transcriber_worker.s3_service")


class S3Service:

    def __init__(self):
       
        self.s3_client = boto3.client('s3')

    @staticmethod
    def parse_bucket_and_key(url: str) -> tuple[str, str]:
        """
        Parses bucket and key from either s3:// bucket/key or HTTP/HTTPS S3 presigned URLs.
        """
        parsed = urlparse(url)
        scheme = parsed.scheme.lower()

        if scheme == 's3':
            bucket = parsed.netloc
            key = parsed.path.lstrip('/')
            return bucket, key
        elif scheme in ('http', 'https'):
            host = parsed.netloc.lower()
            path = parsed.path.lstrip('/')

            if 's3' in host:
                if host.startswith('s3.') or host == 's3.amazonaws.com':
                    path_parts = path.split('/', 1)
                    if len(path_parts) < 2:
                        raise ValueError(f"Could not parse path-style S3 URL path: {path}")
                    bucket = path_parts[0]
                    key = path_parts[1]
                else:
                    host_parts = parsed.netloc.split('.s3', 1)
                    bucket = host_parts[0]
                    key = path
                return bucket, key
            else:
                raise ValueError(f"Unable to parse S3 bucket/key from HTTP URL: {url}")
        else:
            raise ValueError(f"Unsupported URL scheme: {url}")

    @staticmethod
    def parse_s3_url(s3_url: str) -> tuple[str, str]:
        """
        Parses an s3://bucket/key URL into (bucket, key).
        """
        parsed = urlparse(s3_url)
        if parsed.scheme.lower() != 's3':
            raise ValueError(f"Invalid S3 URL scheme (expected 's3'): {s3_url}")
        bucket = parsed.netloc
        key = parsed.path.lstrip('/')
        return bucket, key

    def download_file(self, video_url: str, local_path: str):
        """
        Downloads a file from either an s3:// URL or an HTTP/HTTPS presigned URL.
        """
        parsed = urlparse(video_url)
        scheme = parsed.scheme.lower()

        if scheme == 's3':
            bucket, key = self.parse_s3_url(video_url)
            logger.info(f"Downloading s3://{bucket}/{key} to {local_path}...")
            self.s3_client.download_file(bucket, key, local_path)
        elif scheme in ('http', 'https'):
            logger.info(f"Downloading presigned HTTP/HTTPS URL to {local_path}...")
            req = urllib.request.Request(
                video_url,
                headers={'User-Agent': 'Mozilla/5.0'}
            )
            with urllib.request.urlopen(req) as response, open(local_path, 'wb') as out_file:
                shutil.copyfileobj(response, out_file)
        else:
            raise ValueError(f"Unsupported URL scheme for download: {video_url}")

    def upload_json(self, bucket: str, key: str, data: dict):
        """
        Uploads a dictionary as a JSON file to S3.
        """
        logger.info(f"Uploading JSON to s3://{bucket}/{key}...")
        self.s3_client.put_object(
            Bucket=bucket,
            Key=key,
            Body=json.dumps(data, indent=2),
            ContentType='application/json'
        )

    def read_json(self, bucket: str, key: str) -> dict:
        """
        Reads and parses a JSON file from S3.
        """
        logger.debug(f"Reading JSON from s3://{bucket}/{key}...")
        response = self.s3_client.get_object(Bucket=bucket, Key=key)
        return json.loads(response['Body'].read().decode('utf-8'))

    def list_keys_with_prefix(self, bucket: str, prefix: str) -> list[str]:
        """
        Lists keys in an S3 bucket starting with the given prefix.
        """
        logger.info(f"Listing objects in s3://{bucket}/{prefix}...")
        paginator = self.s3_client.get_paginator('list_objects_v2')
        keys = []
        for page in paginator.paginate(Bucket=bucket, Prefix=prefix):
            for obj in page.get('Contents', []):
                keys.append(obj['Key'])
        return keys
