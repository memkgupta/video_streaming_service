from config import S3_BUCKET
import boto3
from botocore.exceptions import ClientError
def get_video_url_presigned(key:str):
  s3_client = boto3.client("s3")
  try:
        url = s3_client.generate_presigned_url(
            ClientMethod="get_object",
            Params={
                "Bucket": S3_BUCKET,
                "Key": key,
            },
            ExpiresIn=86400,
        )
        return url

  except ClientError as e:
        raise RuntimeError(f"Failed to generate presigned URL: {e}")   