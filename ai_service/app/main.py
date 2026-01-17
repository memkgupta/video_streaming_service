import threading
import signal
import sys
from fastapi import FastAPI
from app.api.callback import router as callback_router
from app.consumer import start_consumer

app = FastAPI()
app.include_router(callback_router)

consumer_thread = None


def run_consumer():
    try:
        start_consumer()
    except Exception as e:
        print("Consumer stopped:", e)


@app.on_event("startup")
def startup_event():
    global consumer_thread
    print("Starting RabbitMQ consumer in background...")
    consumer_thread = threading.Thread(target=run_consumer, daemon=True)
    consumer_thread.start()


@app.on_event("shutdown")
def shutdown_event():
    print("Shutting down main service...")


def shutdown_handler(sig, frame):
    print("Stopping service...")
    sys.exit(0)


signal.signal(signal.SIGINT, shutdown_handler)
signal.signal(signal.SIGTERM, shutdown_handler)
