from fastapi import File, UploadFile
from pydantic import BaseModel

class UploadRequest(BaseModel):
    user_id: int
    chat_id: int
    file: UploadFile=File(...)