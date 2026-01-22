from pydantic import BaseModel


class AssetChunk(BaseModel):
    assetId:str
    chunkId:int
    size:int
    start:int
    end:int