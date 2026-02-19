import json
from enum import Enum
from dto.video_update import UpdateRequestDTO
from dto.moderation_result import ModerationResult , ModerationStatus , ModerationFlag
def assert_json_safe(value, path="root"):
    """
    Recursively ensure value contains ONLY JSON-safe types.
    """
    if isinstance(value, dict):
        for k, v in value.items():
            assert_json_safe(v, f"{path}.{k}")
    elif isinstance(value, list):
        for i, v in enumerate(value):
            assert_json_safe(v, f"{path}[{i}]")
    else:
        assert isinstance(
            value,
            (str, int, float, bool, type(None)),
        ), f"❌ Non-JSON type at {path}: {type(value)}"


def kafka_value_serializer(obj):
    """
    EXACT replica of KafkaProducer value_serializer
    """
    return json.dumps(obj.to_dict()).encode("utf-8")

def test_update_request_serialization():
    result = ModerationResult(
        id="mod_001",
        status=ModerationStatus.SAFE,
        confidence_score=0.97,
        flags=[ModerationFlag.NSFW],
        metadata={"source": "vision-model"},
        video_id="vid_123",
    )

    update = UpdateRequestDTO(
        video_id="vid_123",
        type="MODERATION",  # string on purpose (real-world case)
        moderation_result=result,
    )

    # Step 1: dict conversion
    payload = update.to_dict()

    # Step 2: ensure JSON-safe
    assert_json_safe(payload)

    # Step 3: simulate Kafka serializer
    kafka_bytes = kafka_value_serializer(update)

    assert isinstance(kafka_bytes, bytes)

    print("✅ UpdateRequestDTO serialization OK")
    print(kafka_bytes.decode("utf-8"))

def test_moderation_result_serialization():
    result = ModerationResult(
        id="mod_001",
        status=ModerationStatus.SAFE,
        confidence_score=0.97,
        flags=[ModerationFlag.NSFW, ModerationFlag.VIOLENCE],
        metadata={
            "model": "yolo-v8",
            "latency_ms": 123,
        },
        video_id="vid_123",
    )

    # Step 1: dict conversion
    payload = result.to_dict()

    # Step 2: ensure JSON-safe
    assert_json_safe(payload)

    # Step 3: JSON dump
    json_bytes = json.dumps(payload).encode("utf-8")

    assert isinstance(json_bytes, bytes)

    print("✅ ModerationResult serialization OK")
    print(json_bytes.decode("utf-8"))

if __name__ == "__main__":
    test_moderation_result_serialization()
    test_update_request_serialization()
    print("🔥 ALL SERIALIZATION TESTS PASSED")
