# app/pipeline/aggregator.py
from dto.asset_chunks import AssetChunk
from typing import List
def aggregate_frame_results(results):
    nsfw_scores = [r["nsfw_score"] for r in results if "nsfw_score" in r]
    weapons = [w for r in results for w in r.get("weapons", [])]
    ocr = [t for r in results for t in r.get("ocr_text", [])]

    return {
        "avg_nsfw": sum(nsfw_scores) / len(nsfw_scores) if nsfw_scores else 0,
        "max_nsfw": max(nsfw_scores) if nsfw_scores else 0,
        "weapons": list(set(weapons)),
        "ocr_text": list(set(ocr))
    }
