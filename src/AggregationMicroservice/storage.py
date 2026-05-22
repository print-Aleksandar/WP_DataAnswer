import psycopg2
from psycopg2.extras import Json
import os

DB_HOST = os.getenv('DB_HOST')
DB_PORT = os.getenv('DB_PORT', '5432')
DB_NAME = os.getenv('DB_NAME')
DB_USERNAME = os.getenv('DB_USERNAME')
DB_PASSWORD = os.getenv('DB_PASSWORD')

class DB_Storage:
    def __init__(self):
        # Postavuvanje konekcija do baza
        print(f'[DB_Storage.__init__]: {DB_HOST}:{DB_PORT} name={DB_NAME} {DB_USERNAME}@{DB_PASSWORD}')

        self.con = psycopg2.connect(
                host=DB_HOST,
                port=DB_PORT,
                dbname=DB_NAME,
                user=DB_USERNAME,
                password=DB_PASSWORD
            )
        self.con.autocommit = False


    def init_db(self):
        # kreiranje tabeli
        with self.con.cursor() as cur:
            with open("sql/create_tables.sql") as f:
                cur.execute(f.read())

        self.con.commit()
    
    def save_data(self, user_id, chat_id, data):
        with self.con.cursor() as cur:
            cur.execute(
            """
                INSERT INTO aggregation_conversions(user_id, chat_id, json_data)
                VALUES (%s, %s, %s)
            """,
            (user_id, chat_id, Json(data))
            )
        
        self.con.commit()
    
    def get_data(self, user_id, chat_id):
        with self.con.cursor() as cur:
            cur.execute(
                "SELECT json_data from aggregation_conversions WHERE user_id=%s AND chat_id=%s", 
                (user_id, chat_id)
            )

            result = cur.fetchone()
            return result[0] if result else None
        
    def close(self):
        self.con.close()

db = DB_Storage()
db.init_db()
db.close()