# app/pipeline/decision.py
def decide(summary: dict) -> str:
    if summary["max_nsfw"] > 0.6:
        return "REVIEW"
    if summary["weapons"]:
        return "REVIEW"
    if summary["ocr_text"]:
        return "REVIEW"
    return "SAFE"
