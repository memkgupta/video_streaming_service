import boto3
import json
from app.config import AWS_ACCESSID , AWS_SECRET , S3_REPORT_BUCKET

def upload_json_to_s3(
   
    key: str,
    data: dict,
    region: str = "ap-south-1",
     bucket: str = S3_REPORT_BUCKET,
):
    s3 = boto3.client("s3",aws_access_key_id=AWS_ACCESSID,
    aws_secret_access_key=AWS_SECRET,region_name=region)

    json_bytes = json.dumps(data, ensure_ascii=False).encode("utf-8")

    s3.put_object(
        Bucket=bucket,
        Key=key,
        Body=json_bytes,
        ContentType="application/json"
    )