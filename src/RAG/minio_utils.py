from io import BufferedReader

from minio import Minio
import os

MINIO_CLIENT, MINIO_BUCKET = None, ''

class MinioClient:
    def __init__(self):
        self.minio_url = os.getenv("MINIO_URL")
        self.access_key = os.getenv("MINIO_ACCESS_KEY")
        self.secret_key = os.getenv("MINIO_SECRET_KEY")
        self.bucket = os.getenv("MINIO_BUCKET")
        
        if not all([self.minio_url, self.access_key, self.secret_key, self.bucket]):
            raise ValueError("minio credentails not fount!")
        
        # parse url
        host = self.minio_url.replace("https://", "").replace("http://", "")
        
        self.client = Minio(
            host,
            access_key=self.access_key,
            secret_key=self.secret_key,
            secure=self.minio_url.startswith("https")
        )
    
    def write_entry(self, index_buffer:BufferedReader,chunks_buffer:BufferedReader,  path:str):
        # Save index
        self.client.put_object(
            self.bucket,
            f'{path}/index.faiss',
            index_buffer,
            length=len(index_buffer.getvalue())
        )
        
        # Save chunks
        self.client.put_object(
            self.bucket,
            f'{path}/chunks.pkl',
            chunks_buffer,
            length=len(chunks_buffer.getvalue())
        )
    
    def read_entry(self, path:str):    
        index_response = self.client.get_object(self.bucket, f'{path}/index.faiss')
        chunk_response = self.client.get_object(self.bucket, f'{path}/chunks.pkl')

        return index_response, chunk_response