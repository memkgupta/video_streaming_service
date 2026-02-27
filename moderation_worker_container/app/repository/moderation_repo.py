# app/repository/moderation_repo.py
import uuid
import json
from dto.moderation_result import ModerationResult,ModerationFlag,ModerationStatus
from dataclasses import asdict
class ModerationRepository:

    def save(self, report: dict,video_id:str) -> ModerationResult:
        report_id = str(uuid.uuid4())
        flags = [
        flag
        for v in report.get("flags", [])
        if (flag := ModerationFlag.from_str(v)) is not None
    ]
        metadata = report.get('metadata',{})
        moderation_result = ModerationResult(
            id=report_id,
           
            # metadata=metadata,
            status=report.get("status",ModerationStatus.SAFE.value),
            flags=[v.value for v in flags],
            video_id=video_id,
            confidence_score=report.get("confidence_score"),
        
        )
        with open(f"/tmp/{report_id}.json", "w") as f:
            json.dump(asdict(moderation_result), f)

        return moderation_result
