from fastapi import FastAPI
from math import sin, cos, radians
import json

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "Hello FastAPI!"}

@app.get("/compute")
def compute(speed: float, angle: float):
    angle_rad = radians(angle)
    impact_time = 2.0 * speed * sin(angle_rad) / 9.81

    points = []
    t = 0.0
    while t < impact_time:
        x = speed * t * cos(angle_rad)
        y = speed * t * sin(angle_rad) - 0.5 * 9.81 * t * t
        points.append({"x": x, "y": y, "time": t})
        t += 0.1
    points.append({"x": speed * impact_time * cos(angle_rad),
                   "y": 0.0,
                   "time": impact_time})

    return {"points": points}
# py -m uvicorn server:app --reload --host 0.0.0.0 --port 8000
 # http://127.0.0.1:8000/docs