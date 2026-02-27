import os
import json
import pika


KAFKA_HOST = os.getenv("KAFKA_HOST","kafka:9092")

S3_BUCKET = os.getenv("S3_ASSET_BUCKET", "videos")
S3_REPORT_BUCKET = os.getenv("S3_REPORT_BUCKET","moderation/reports")
AWS_ACCESSID = os.getenv("AWS_ACCESS_ID", "")
AWS_SECRET = os.getenv("AWS_SECRET", "")
JOB_ID = os.getenv("JOB_ID","")
ASSET_URL = os.getenv("ASSET_URL","")
ASSET_SIZE = os.getenv("ASSET_SIZE",0)