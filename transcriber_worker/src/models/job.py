from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class TranscriptionJob(BaseModel):
    model_config = ConfigDict(
        alias_generator=to_camel,
        populate_by_name=True,
    )
    asset_id: str
    media_id: str
    chunk_url: str
    chunk_number: int
    start_time: float
    end_time: float
   