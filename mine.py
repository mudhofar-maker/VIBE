from fastapi import FastAPI, File, UploadFile, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
import shutil
import os
from datetime import datetime

app = FastAPI(title="VibeStream Backend", version="1.0")

# Mengizinkan akses dari frontend manapun (CORS)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Folder penyimpanan video lokal di HP
UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)

# Mount folder uploads agar bisa diakses sebagai file statis
app.mount("/uploads", StaticFiles(directory=UPLOAD_DIR), name="uploads")

# Database sementara (Mock Database di memori)
feed_database = []

@app.get("/")
def read_root():
    return {"message": "VibeStream Python Backend (mine.py) is running successfully!"}

# Endpoint untuk Upload Video Animasi
@app.post("/api/upload")
async def upload_video(
    file: UploadFile = File(...),
    username: str = Form(...),
    caption: str = Form(...)
):
    try:
        # Buat nama file unik berdasarkan waktu
        file_extension = file.filename.split(".")[-1]
        unique_filename = f"{int(datetime.now().timestamp())}_{username}.{file_extension}"
        file_path = os.path.join(UPLOAD_DIR, unique_filename)

        # Simpan file ke penyimpanan lokal HP
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)

        # URL akses video
        video_url = f"/uploads/{unique_filename}"

        # Data postingan baru
        post_data = {
            "id": len(feed_database) + 1,
            "username": username,
            "caption": caption,
            "video_url": video_url,
            "created_at": datetime.now().isoformat()
        }

        # Masukkan ke database sementara
        feed_database.insert(0, post_data)

        return {
            "status": "success",
            "message": "Video berhasil diunggah ke feed!",
            "data": post_data
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Endpoint untuk Mengambil Daftar Feed
@app.get("/api/feed")
def get_feed():
    return {
        "status": "success",
        "data": feed_database
    }
