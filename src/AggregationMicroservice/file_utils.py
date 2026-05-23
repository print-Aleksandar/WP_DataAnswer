import pandas as pd
from fastapi import UploadFile, File, HTTPException
from io import BytesIO
import json
from storage import DB_Storage

FILETYPE_MAPPER = {
    'xlsx': 'read_excel',
    'xls': 'read_excel',
    'ods': 'read_excel',
    'csv': 'read_csv',
    'json': 'read_json',
    # 'xml': 'read_xml' # Not reliable so it is removed for now
}

async def parse_file(file:UploadFile):
    # Get parser function for filetype
    extention = file.filename.split('.')[-1]
    parse_fn = FILETYPE_MAPPER.get(extention)

    # check if filetype is supported
    if parse_fn is None: raise TypeError(f"Unsuported filetype: '{extention}'" )

    # read content from file
    content = await file.read()  

    # get readre function from pandas
    reader_fn = getattr(pd, parse_fn)

    # Load into dataframe
    df = reader_fn(BytesIO(content))

    # Skip empty cols
    df.dropna(axis=1, how='all', inplace=True)

    # Convert to JSON
    return df.to_json(orient="records")

async def handle_upload(user_id:int, chat_id:int, file: UploadFile = File(...), *args):
    """
    Handle file upload, parsing and storage 
    """
    try:
        # Read file
        data = await parse_file(file)

        json_data = json.loads(data)

        db = DB_Storage()

        db.save_data(user_id, chat_id, json_data)


        db.close()

        return json_data

    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

def get_supported_filetypes():
    return list(FILETYPE_MAPPER.keys())