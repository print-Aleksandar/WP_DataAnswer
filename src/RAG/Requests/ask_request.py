from pydantic import BaseModel, Field


class AskRequest(BaseModel):
    user_id : int = Field(...)
    question : str = Field(...)
    top_k : int = Field(default=5, ge=1, le=20)
    chat_id : int = Field(...)