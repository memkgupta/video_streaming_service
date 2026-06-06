from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    kafka_bootstrap_servers: str
    kafka_transcript_topic: str
    queue_job_name: str
    queue_server:str
    queue_port:str
    cdn_url:str
    bucket_name:str
    class Config:
        env_file = ".env"


settings = Settings()