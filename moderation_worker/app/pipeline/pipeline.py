#Step 1 -> 1. Chunking
#Step 2 -> 2. Push Chunks to the queue
#Step 3 -> 3. Process Chunks
#Step 4 -> 4. Aggregate Result


from typing import List, Dict
from datetime import datetime, timezone
from core.chunker import VirtualChunker
from dtos.chunk_result import ChunkResult
from core.chunk_worker import ChunkWorker
from models.moderation import DefaultModerationModels
from dtos.dtos import ModerationResult  , ContentType , ModerationStatus , ModerationFlag ,ModerationCategory

class ResultAggregator:

    def aggregate(
        self,
        results: List[ChunkResult],
        job_id: str,
        asset_id: str,
        content_type: ContentType,
        processing_ms: int = 0,
        metadata: dict | None = None,
    ) -> ModerationResult:

        if not results:
            return ModerationResult(
                job_id=job_id,
                asset_id=asset_id,
                content_type=content_type,
                status=ModerationStatus.APPROVED,
                overall_score=0.0,
                chunk_results=[],
                chunks_total=0,
                chunks_flagged=0,
                violation_count=0,
                processing_ms=processing_ms,
                metadata=metadata or {},
            )

        total_chunks = len(results)

        max_nsfw = max(r.nsfw_score for r in results)
        max_violence = max(r.violence_score for r in results)
        max_hate = max(r.hate_score for r in results)

        avg_nsfw = sum(r.nsfw_score for r in results) / total_chunks
        avg_violence = sum(r.violence_score for r in results) / total_chunks
        avg_hate = sum(r.hate_score for r in results) / total_chunks


        flagged_chunks = [
            r for r in results
            if r.nsfw_score > 0.8
            or r.violence_score > 0.8
            or r.hate_score > 0.8
        ]

        chunks_flagged = len(flagged_chunks)
        total_violations = sum(r.violation_count for r in results)

        flags: list[ModerationFlag] = []

        if max_nsfw > 0.6:
            flags.append(ModerationFlag(category=ModerationCategory.NSFW,score=max_nsfw))

        if max_violence > 0.6:
            flags.append(ModerationFlag(category=ModerationCategory.VIOLENCE,score=max_violence))

        if max_hate > 0.6:
            flags.append(ModerationFlag(category=ModerationCategory.HATE,score=max_hate))


        overall_score = max(
            max_nsfw,
            max_violence,
            max_hate
        )


        if overall_score > 0.7:
            status = ModerationStatus.REJECTED
        elif flags:
            status = ModerationStatus.FLAGGED
        else:
            status = ModerationStatus.APPROVED


        return ModerationResult(
            job_id=job_id,
            asset_id=asset_id,
            content_type=content_type,
            status=status,
            overall_score=overall_score,
            flags=flags,
            chunk_results=results,
            chunks_total=total_chunks,
            chunks_flagged=chunks_flagged,
            violation_count=total_violations,
            processing_ms=processing_ms,
            metadata={
                "max_nsfw": max_nsfw,
                "max_violence": max_violence,
                "max_hate": max_hate,
                "avg_nsfw": avg_nsfw,
                "avg_violence": avg_violence,
                "avg_hate": avg_hate,
                **(metadata or {})
            },
            completed_at=datetime.now(timezone.utc),
        )
     
class ModerationPipeline:

    def __init__(self):
        self.chunker = VirtualChunker(chunk_length=10, overlap=2)
        self.models = DefaultModerationModels()
        self.worker = ChunkWorker(models=self.models)
        self.aggregator = ResultAggregator()

    def run(self, video_id: str, video_url: str, job_id:str , asset_id:str,content_type:ContentType):
        print("\n🚀 Starting Moderation Pipeline\n")

        # ───────────────────────────────────────────────
        # Step 1 → Chunking
        # ───────────────────────────────────────────────
        print("📦 Step 1: Chunking video...")
        chunks = self.chunker.generate_chunks(video_url)
        print(f"Generated {len(chunks)} chunks\n")

        # ───────────────────────────────────────────────
        # Step 2 → Queue (SKIPPED for now)
        # ───────────────────────────────────────────────
        print("📨 Step 2: Queue skipped (running sync mode)\n")

        # ───────────────────────────────────────────────
        # Step 3 → Process Chunks
        # ───────────────────────────────────────────────
        print("⚙️ Step 3: Processing chunks in parallel...\n")

        results = []
        
        import concurrent.futures

        def _process_single_chunk(chunk):
            print(f"→ Processing chunk {chunk.chunk_id} "
                  f"[{chunk.start_time}-{chunk.end_time}]")
            return self.worker.process_chunk(
                video_url=video_url,
                chunk_id=chunk.chunk_id,
                start_time=chunk.start_time,
                end_time=chunk.end_time,
            )

        # Using ThreadPoolExecutor to run chunk processing concurrently
        with concurrent.futures.ThreadPoolExecutor(max_workers=4) as executor:
            future_to_chunk = {executor.submit(_process_single_chunk, chunk): chunk for chunk in chunks}
            
            for future in concurrent.futures.as_completed(future_to_chunk):
                chunk = future_to_chunk[future]
                try:
                    result = future.result()
                    results.append(result)
                except Exception as exc:
                    print(f"Chunk {chunk.chunk_id} generated an exception: {exc}")

        # ───────────────────────────────────────────────
        # Step 4 → Aggregate Results
        # ───────────────────────────────────────────────
        print("\n📊 Step 4: Aggregating results...\n")

        final_report = self.aggregator.aggregate(results,job_id=job_id,asset_id=asset_id,content_type=content_type)

        return final_report